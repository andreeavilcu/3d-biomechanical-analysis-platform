import open3d as o3d
import numpy as np
import os
import sys
import tempfile
from flask import Flask, request, jsonify

# Flask Configuration
app = Flask(__name__)

# Encoding Configuration (for Windows compatibility)
try:
    sys.stdout.reconfigure(encoding='utf-8')
    sys.stderr.reconfigure(encoding='utf-8')
except AttributeError:
    pass

def load_and_normalize(file_path, is_polycam=True):
    """ Loads and pre-processes the point cloud. """
    print(f"--> [SERVER] Loading file: {file_path}")
    
    try:
        pcd = o3d.io.read_point_cloud(file_path)
    except Exception as e:
        print(f"Read error: {e}")
        return None

    if pcd.is_empty():
        return None

    # Polycam Logic
    if is_polycam:
        print("   [INFO] Fine downsampling (Voxel: 0.015)...")
        pcd = pcd.voxel_down_sample(voxel_size=0.015) 
        
        # Automatic scale verification
        bbox = pcd.get_axis_aligned_bounding_box()
        # If height (Z-axis) is less than 1 meter, assume scale issue
        if bbox.get_extent()[2] < 1.0: 
             print("   [INFO] Small scale scan detected (< 1m). Applying x2 scaling...")
             pcd.scale(2.0, center=bbox.get_center())

    return pcd

def process_point_cloud_logic(file_path):
    """ Main processing logic (no GUI visualization). """
    
    # 1. Load
    pcd = load_and_normalize(file_path, is_polycam=True)
    if pcd is None:
        return {"error": "Could not load point cloud"}

    # 2. Filtering (Skip RANSAC for Polycam - keep everything to avoid removing feet/floor data)
    objects_cloud = pcd
    
    if len(objects_cloud.points) == 0:
        return {"error": "Cloud is empty after filtering"}

    # 3. Clustering (DBSCAN)
    print("   [SERVER] Applying DBSCAN...")
    # eps=0.3 and min_points=30 are heuristic values for human-scale objects
    labels = np.array(objects_cloud.cluster_dbscan(eps=0.3, min_points=30, print_progress=False))

    if labels.size == 0 or labels.max() < 0:
        return {"error": "No clusters found"}

    max_label = labels.max()
    human_cluster = None

    # 4. Human Identification
    print(f"   [SERVER] Found {max_label + 1} clusters. Searching for human...")
    
    for i in range(max_label + 1):
        cluster_indices = np.where(labels == i)[0]
        if cluster_indices.size == 0: continue
        
        cluster = objects_cloud.select_by_index(cluster_indices)
        bbox = cluster.get_axis_aligned_bounding_box()
        extent = bbox.get_extent()
        h = extent[2] # Height (Z)
        w = max(extent[0], extent[1]) # Max width (X or Y)

        # Detection logic: Filter based on human dimensions
        # Height between 0.5m and 2.5m, Width less than 1.2m
        if 0.5 < h < 2.5 and w < 1.2:
            print(f"   --> CANDIDATE ACCEPTED (H={h:.2f}m)!")
            human_cluster = cluster
            break 

    if human_cluster is None:
        return {"error": "No human-like object detected"}

    # 5. Keypoint Extraction (Geometric Approximation)
    # Note: This is not ML pose estimation, but geometric estimation based on the bounding box.
    points = np.asarray(human_cluster.points)
    min_z = np.min(points[:, 2])
    max_z = np.max(points[:, 2])
    center_x = np.mean(points[:, 0])
    center_y = np.mean(points[:, 1])
    height = max_z - min_z

    keypoints = {
        "head":       {"x": float(center_x), "y": float(center_y), "z": float(max_z - (height * 0.1))},
        "neck":       {"x": float(center_x), "y": float(center_y), "z": float(max_z - (height * 0.2))},
        "l_shoulder": {"x": float(center_x - 0.2), "y": float(center_y), "z": float(max_z - (height * 0.25))},
        "r_shoulder": {"x": float(center_x + 0.2), "y": float(center_y), "z": float(max_z - (height * 0.25))},
        "pelvis":     {"x": float(center_x), "y": float(center_y), "z": float(min_z + (height * 0.55))},
        "l_knee":     {"x": float(center_x - 0.1), "y": float(center_y + 0.1), "z": float(min_z + (height * 0.3))},
        "r_knee":     {"x": float(center_x + 0.1), "y": float(center_y + 0.1), "z": float(min_z + (height * 0.3))},
        "l_ankle":    {"x": float(center_x - 0.1), "y": float(center_y), "z": float(min_z + 0.05)},
        "r_ankle":    {"x": float(center_x + 0.1), "y": float(center_y), "z": float(min_z + 0.05)},
        "meta":       {"detected_height": float(height)}
    }
    
    return keypoints

@app.route('/process-scan', methods=['POST'])
def process_scan():
    """ API Endpoint called by Java/External Client """
    if 'file' not in request.files:
        return jsonify({"error": "No file part"}), 400
    
    file = request.files['file']
    if file.filename == '':
        return jsonify({"error": "No selected file"}), 400

    if file:
        # Save temporary file so Open3D can read it from disk
        try:
            with tempfile.NamedTemporaryFile(delete=False, suffix=".ply") as tmp:
                file.save(tmp.name)
                tmp_path = tmp.name
            
            # Process the file
            result = process_point_cloud_logic(tmp_path)
            
            # Clean up temporary file
            os.remove(tmp_path)
            
            # Return JSON result
            return jsonify(result)

        except Exception as e:
            return jsonify({"error": str(e)}), 500

@app.route('/health', methods=['GET'])
def health_check():
    """ Health check endpoint """
    return jsonify({"status": "healthy", "service": "python-processing"}), 200

if __name__ == '__main__':
    # Start the server on port 5000
    print(">>> Starting AI Python server on port 5000...")
    app.run(host='0.0.0.0', port=5000, debug=True)