const fs = require("fs");
const express = require("express");

let ContentType = {
    "jpg": "image/jpeg", 
    "jpeg": "image/jpeg", 
    "png": "image/png", 
    "obj": "text/plain", 
    "mtl": "text/plain", 
    "mp3": "audio/mp3", 
    "wav": "audio/wav", 
    "ogg": "audio/ogg"
}

let server = express();

server.get("/index", function(req, res) {
    res.setHeader("Content-Type", "text/html;charset=utf-8");
    fs.readFile(__dirname+"/assets/webpage/index.html", function(err, data){
        res.send(data.toString());
        res.end();
    })
})

server.get("/*.js", function(req, res) {
    res.setHeader("Content-Type", "application/javascript");
    res.sendFile(__dirname + req.url);
})

server.get("/model/:name", function(req, res) {
    let tmp = req.params.name.split(".");
    let extension = tmp[tmp.length-1];
    res.setHeader("Content-Type", ContentType[extension]);
    res.sendFile(__dirname+"/assets/model/"+req.params.name);
})

server.get("/model/texture/:name", function(req, res) {
    let tmp = req.params.name.split(".");
    let extension = tmp[tmp.length-1];
    res.setHeader("Content-Type", ContentType[extension]);
    res.sendFile(__dirname+"/assets/texture/"+req.params.name);
})

server.get("/music/:name", function(req, res) {
    let tmp = req.params.name.split(".");
    let extension = tmp[tmp.length-1];
    res.setHeader("Content-Type", ContentType[extension]);
    res.sendFile(__dirname+"/assets/sound/"+req.params.name);
})

let settings = undefined;

server.get("/change_settings", function(req, res) {
    res.setHeader("Content-Type", "text/plain");
    res.end();

    settings = {type: req.query.type};
    switch (settings.type) {
        case "state":
            if (req.query.do_play=="0") {
                settings.do_play = false;
            }
            else {
                settings.do_play = true;
            }
            settings.play_time = parseInt(req.query.play_time) * 60000;
            break

        case "model":
            settings.obj_path = req.query.obj_path;
            settings.mtl_path = req.query.mtl_path;
            break;

        case "music":
            settings.snd_path = req.query.snd_path;
            break;

        case "display_range":
            settings.display_range = parseFloat(req.query.value).toFixed(1);
            break;

        case "rotational_speed":
            settings.rotational_speed = parseInt(req.query.value) / 60 * 2 * Math.PI;
            break;

        case "rotational_direction":
            settings.rotational_direction = parseInt(req.query.value);
            break;

        default:
            break;
    }
})

server.get("/get_settings", function(req, res) {
    res.setHeader("Content-Type", "application/json");
    res.send(settings);
    settings = undefined;
    res.end();
})

server.listen(80, function() {
    console.log("server started");
})