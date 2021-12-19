import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';
import { environment } from './environments/environment';

if (environment.production) {
  enableProdMode();
}

platformBrowserDynamic()
  .bootstrapModule(AppModule)
  .catch((err) => console.error(err));

  let interval = setInterval(function() {

    let buttons = document.getElementsByClassName("buttonFinish");
      
    for(let i=0; i<buttons.length; i++) {
      buttons[i].addEventListener("click", function() {
        console.log("viknat")
    })
    clearInterval(interval);
    }

    

  }, 1000);
