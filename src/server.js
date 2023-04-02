const fs = require("fs");
const express = require("express");

let ContentType = {
    "jpg": "image/jpeg", 
    "jpeg": "image/jpeg", 
    "png": "image/png", 
    "obj": "text/plain", 
    "mtl": "text/plain"
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

let settings = undefined;

server.get("/change_model", function(req, res) {
    res.setHeader("Content-Type", "text/plain");
    res.end();
    settings = {
        obj_path: req.query.obj_path, 
        mtl_path: req.query.mtl_path
    };
})

server.get("/get_model", function(req, res) {
    res.setHeader("Content-Type", "application/json");
    res.send(settings);
    settings = undefined;
    res.end();
})

server.listen(80, function() {
    console.log("server started");
})