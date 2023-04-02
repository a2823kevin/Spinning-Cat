import * as THREE from "three";
import {OBJLoader} from "three/addons/loaders/OBJLoader.js";
import {MTLLoader} from 'three/addons/loaders/MTLLoader.js';

function get_obj_bbox(obj) {
    let box = new THREE.Box3().setFromObject(obj);
    return [box.min.x, box.max.x, box.min.y, box.max.y, box.min.z, box.max.z];
}

function get_obj_size(bbox) {
    let size = [bbox[1]-bbox[0], bbox[3]-bbox[2], bbox[5]-bbox[4]];
    return {
        x: size[0], 
        y: size[1], 
        z: size[2], 
        largest_side: Math.max(...size)
    };
}

let scene = new THREE.Scene();

let renderer = new THREE.WebGLRenderer({ alpha: true });
renderer.setSize(window.innerWidth*0.95, window.innerHeight*0.95);
renderer.shadowMap.enable = true;

document.body.appendChild(renderer.domElement);

let camera = new THREE.PerspectiveCamera(50, window.innerWidth/window.innerHeight, 0.1, 100);

let pointLight = new THREE.PointLight(0xffffff);
scene.add(pointLight);

let model = undefined;

const objloader = new OBJLoader();
const mtlloader = new MTLLoader();

let fps = 60;
let interval = 1000 / fps;
let now;
let then;

function animate() {
    if (model!=undefined) {
        model.rotation.y = (model.rotation.y+(Math.PI*2/fps)) % (Math.PI*2);
    }
}

function render(now) {
    if (!then) {
        then = now;
    }

    requestAnimationFrame(render);
    if (now-then>interval) {
        then = now - ((now-then)%interval);
        animate();
        renderer.render(scene, camera);
    }
}

function load_model(obj_path, mtl_path) {
    if (model!=undefined) {
        scene.remove(model);
    }

    if (mtl_path!=undefined) {
        mtlloader.load(mtl_path, (mtl) => {
            objloader.setMaterials(mtl);
        });
    }
    objloader.load(obj_path, (obj) => {
        model = obj;
        model.rotation.x -= Math.PI / 2;

        //set model to center
        model.position.set(0, 0, 0);
        let bbox = get_obj_bbox(model);
        let model_size = get_obj_size(bbox);
        model.position.set(-(bbox[0]+bbox[1])/2, -(bbox[3]+bbox[2])/2, -(bbox[5]+bbox[4])/2);
        scene.add(model);

        camera.position.setY(model_size.largest_side*1.5);
        camera.lookAt(scene.position);
        pointLight.position.setY(model_size.largest_side*1.5);
    });
}

render();

//export
window.load_model = load_model;