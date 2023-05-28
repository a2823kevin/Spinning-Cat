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
server.use(express.json());

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

server.get("/icon/:name", function(req, res) {
    let tmp = req.params.name.split(".");
    let extension = tmp[tmp.length-1];
    res.setHeader("Content-Type", ContentType[extension]);
    res.sendFile(__dirname+"/assets/icon/"+req.params.name);
})

server.get("/music/:name", function(req, res) {
    let tmp = req.params.name.split(".");
    let extension = tmp[tmp.length-1];
    res.setHeader("Content-Type", ContentType[extension]);
    res.sendFile(__dirname+"/assets/sound/"+req.params.name);
})

let settings = undefined;

server.post("/settings/state", function(req, res) {
    res.setHeader("Content-Type", "text/plain");
    res.end();
    req.body.type = "state";
    req.body.play_time *= 60000;
    settings = req.body;
});

server.post("/settings/model", function(req, res) {
    res.setHeader("Content-Type", "text/plain");
    res.end();
	if (req.body.mtl_path=="-") {
		req.body.mtl_path = undefined;
	}
    req.body.type = "model";
    settings = req.body;
});

server.post("/settings/music", function(req, res) {
    res.setHeader("Content-Type", "text/plain");
    res.end();
    req.body.type = "music";
    settings = req.body;
});

server.post("/settings/rotate/:type/:value", function(req, res) {
    res.setHeader("Content-Type", "text/plain");
    res.end();
    let s = {}
    if (req.params.type=="speed") {
        s.type = "rotational_speed";
        s.rotational_speed = req.params.value / 60 * 2 * Math.PI;
    }
    else if (req.params.type=="direction") {
        s.type = "rotational_direction";
        s.rotational_direction = req.params.value;
    }
    settings = s;
});

server.post("/settings/displayRange/:value", function(req, res) {
    res.setHeader("Content-Type", "text/plain");
    res.end();
    let s = {}
    s.type = "display_range";
    s.display_range = parseFloat(req.params.value).toFixed(1);
    settings = s;
});

server.post("/settings/volume/:value", function(req, res) {
    res.setHeader("Content-Type", "text/plain");
    res.end();
    let s = {}
    s.type = "volume";
    s.volume = req.params.value / 100;
    settings = s;
});

server.post("/settings/canvas/:value", function(req, res) {
    res.setHeader("Content-Type", "text/plain");
    res.end();
    let s = {}
    s.type = "canvas";
    s.ratio = req.params.value;
    settings = s;
});

server.get("/settings", function(req, res) {
    res.setHeader("Content-Type", "application/json");
    res.send(settings);
    settings = undefined;
    res.end();
})

server.listen(80, function() {
    console.log("server started");
})