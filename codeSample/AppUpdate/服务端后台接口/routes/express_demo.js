var express = require('express');
var app = express();
var fs = require("fs");

app.get('/update', function (req, res) {
    fs.readFile( __dirname + "/" + "version.json", 'utf8', function (err, data) {
      //  data = JSON.parse( data );

        console.log( data );
        res.end( data );
    });
})

var server = app.listen(8081, function () {

    var host = server.address().address
    var port = server.address().port

    console.log("应用实例，访问地址为 http://%s:%s", host, port)

})