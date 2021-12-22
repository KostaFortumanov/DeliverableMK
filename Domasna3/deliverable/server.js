const express = require('express');
const path = require('path');
const cors = require('cors');

const app = express();

// add this code
const whitelist = ['https://tesedelivery-api.herokuapp.com/']; // list of allow domain

const corsOptions = {
   origin: function(origin, callback) {
      if (!origin) {
         return callback(null, true);
      }
      return callback(null, true);
   }
}

app.use(cors(corsOptions));
app.use(express.static('./dist/deliverable'));
app.get('/*', (req,res) => {
    res.sendFile('index.html', {root: 'dist/deliverable/'})    
});
app.listen(process.env.PORT || 8080);