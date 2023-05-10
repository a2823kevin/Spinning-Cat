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

let fps = 60;
let interval = 1000 / fps;
let now;
let then;

function animate() {
    if (model!=undefined) {
        model.rotation.y = (model.rotation.y+rotational_direction*rotational_speed/fps) % (Math.PI*2);
    }
}

function render(now) {
    if (!then) {
        then = now;
    }

    requestAnimationFrame(render);
    if (now-then>interval) {
        then = now - ((now-then)%interval);
        if (!pause) {
            animate();
        }
        renderer.render(scene, camera);
    }
}

const objloader = new OBJLoader();
const mtlloader = new MTLLoader();

let pause = true;
let countdown_timeout = undefined;

let music_player = new Audio();
music_player.loop = true;

let model, music;
let range_unit, rotational_speed=2*Math.PI, rotational_direction=-1, music_volume;
let rotate_time;

render();

function change_settings(settings) {
    switch (settings.type) {
        case "state":
            change_state(settings.do_play, settings.play_time);
            break;

        case "model":
            if (settings.obj_path!=model) {
                change_model(settings.obj_path, settings.mtl_path);
            }
            break;

        case "music":
            if (settings.snd_path!=music) {
                change_music(settings.snd_path);
            }
            break;

        case "display_range":
            change_display_range(settings.display_range);
            break;

        case "rotational_speed":
            if (settings.rotational_speed!=rotational_speed) {
                change_rotational_speed(settings.rotational_speed);
            }
            break;

        case "rotational_direction":
            if (settings.rotational_direction!=rotational_direction) {
                change_rotational_direction(settings.rotational_direction);
            }
            break;

        case "music_volume":
            if (settings.value!=music_volume) {
                change_music_volume();
            }
            break;

        default:
            break;
    };
}

function change_state(do_play, play_time) {
    if (do_play) {
        pause = false;
        music_player.play();

        if (countdown_timeout!=undefined) {
            clearTimeout(countdown_timeout);
        }
        if (play_time>0) {
            countdown_timeout = setTimeout(function(){pause=true;music_player.pause();countdown_timeout=undefined;}, play_time);
        }
    }
    else {
        pause = true;
        music_player.pause();
    }
}

function change_model(obj_path, mtl_path) {
    if (model!=undefined) {
        scene.remove(model);
    }

    if (obj_path=="-") {
        return;
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
        range_unit = model_size.largest_side;
        change_display_range(1.7);
    });
}

function change_music(snd_path) {
    if (snd_path=="-") {
        return;
    }
    music = snd_path;

    music_player.src = snd_path;
    if (do_play&&music_player.paused) {
        music_player.play();
    }
};

function change_display_range(value) {
    camera.position.setY(range_unit*value);
    camera.lookAt(scene.position);
    pointLight.position.setY(range_unit*value);
};

function change_rotational_speed(value) {
    rotational_speed = value;
};

function change_rotational_direction(value) {
    rotational_direction = value;
};
function change_music_volume(){};

//export
window.change_settings = change_settings;