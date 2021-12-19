import { OnInit, Component } from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-fullscreen';
import 'leaflet-easybutton';
import { DriverMapService } from 'src/app/services/driver-map.service';
import { finalize } from 'rxjs/operators';

export interface Job {
  id: number;
  address: string;
  description: string;
}

const options = {
  enableHighAccuracy: true,
  maximumAge: 30000,
  timeout: 27000,
};

@Component({
  selector: 'app-driver-map',
  templateUrl: './driver-map.component.html',
  styleUrls: ['./driver-map.component.scss'],
})
export class DriverMapComponent implements OnInit {
  track = false;
  map!: L.Map;
  paths!: any;
  currentLat!: number;
  curentLon!: number;
  show = 0;
  jobs: Job[] = [];
  polylines: L.Polyline[] = [];
  markers: L.Marker[] = [];
  myLocationIcon: L.Icon = L.icon({
    iconUrl: './assets/markerIcons/myLocation.png',
    iconSize: [30, 30],
  });
  currentLocationMarker: L.Marker = L.marker(L.GeoJSON.coordsToLatLng([0, 0]), {
    icon: this.myLocationIcon,
  });

  private initMap(): void {
    this.map = L.map('driverMap', {
      fullscreenControl: true,
      center: [41.9961, 21.4316],
      zoom: 13,
    });

    let trackButton = L.easyButton(
      '<i class="material-icons">navigation</i>',
      (btn, map) => {
        this.track = true;
        this.map.panTo(
          new L.LatLng(
            this.currentLocationMarker.getLatLng().lat,
            this.currentLocationMarker.getLatLng().lng
          )
        );
        trackButton.remove();
        dontTrackButton.addTo(this.map);
      }
    ).addTo(this.map);

    let dontTrackButton = L.easyButton(
      '<i class="material-icons">near_me</i>',
      (btn, map) => {
        this.track = false;
        dontTrackButton.remove();
        trackButton.addTo(this.map);
      }
    );
  }

  private tiles = L.tileLayer(
    'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
    {
      maxZoom: 18,
      minZoom: 3,
      attribution:
        '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    }
  );

  constructor(private driverMapService: DriverMapService) {}

  ngOnInit(): void {
    this.initMap();
    this.tiles.addTo(this.map);
    this.currentLocationMarker.addTo(this.map);

    let paths = window.sessionStorage.getItem('paths');

    if (paths) {
      this.paths = JSON.parse(paths);
      this.show = this.paths.length;
      console.log('if');
      this.init();
      this.drawMap();
    } else {
      this.driverMapService.getPaths()
      .pipe(finalize(() => {
        this.init();
        this.drawMap();
      }))
      .subscribe(
        (data) => {
          console.log('else');
          this.paths = data;
          this.show = this.paths.length;
          console.log(this.show);
          window.sessionStorage.setItem('paths', JSON.stringify(this.paths));

        },
        (error) => {
          this.paths = [];
        }
      );
    }
  }

  init() {
    for (let i = 0; i < this.paths.length; i++) {
      this.polylines.push(
        L.polyline(L.GeoJSON.coordsToLatLngs(this.paths[i]), { color: 'red' })
      );

      let icon;
      if (i == this.paths.length - 1) {
        icon = L.icon({
          iconUrl: `./assets/markerIcons/office.png`,
          iconSize: [30, 30],
        });
      } else {
        icon = L.icon({
          iconUrl: `./assets/markerIcons/number_${i + 1}.png`,
          iconSize: [30, 30],
        });
      }

      let marker = L.marker(
        L.GeoJSON.coordsToLatLng(this.paths[i][this.paths[i].length - 1]),
        { icon: icon }
      );
      this.markers.push(marker);
      this.getJobInfo();
    }

    navigator.geolocation.watchPosition(
      (position) => {
        let lon = position.coords.longitude;
        let lat = position.coords.latitude;

        if (
          this.currentLocationMarker.getLatLng().lng !== lon &&
          this.currentLocationMarker.getLatLng().lat !== lat
        ) {
          this.currentLocationMarker.removeFrom(this.map);
          this.currentLocationMarker = L.marker(
            [position.coords.latitude, position.coords.longitude],
            { icon: this.myLocationIcon }
          );
          this.currentLocationMarker.addTo(this.map);
          this.updateCurrentPath(lon, lat);
          if (this.track) {
            this.map.panTo(new L.LatLng(lat, lon));
          }
        }
      },
      (error) => {},
      options
    );
  }

  getJobInfo() {
    this.driverMapService.getCurrentJobsInfo().subscribe((data) => {
      this.jobs = data;
      for (let i = 0; i < this.jobs.length; i++) {
        this.markers[i].bindPopup(
          `<p>${this.jobs[i].id}</p><p>${this.jobs[i].address}</p><p>${this.jobs[i].description}</p>`
        );
      }
    });
  }

  updateCurrentPath(lon: number, lat: number) {
    if (this.paths.length > 0) {
      let destination = this.paths[0][this.paths[0].length - 1];
      this.driverMapService
        .updateCurrentPath(lon, lat, destination[0], destination[1])
        .subscribe((data) => {
          console.log('refresh');
          this.polylines[0].removeFrom(this.map);
          this.polylines[0] = L.polyline(L.GeoJSON.coordsToLatLngs(data), {
            color: 'red',
          });
          this.polylines[0].addTo(this.map);
        });
    }
  }

  finishJob(id: number) {
    console.log(id);
  }

  drawMap() {
    if (this.show > this.polylines.length) {
      this.show = this.polylines.length;
    }

    if (this.show < 0) {
      this.show = 0;
    }

    this.currentLocationMarker.removeFrom(this.map);

    for (let i = 0; i < this.polylines.length; i++) {
      this.polylines[i].removeFrom(this.map);
      this.markers[i].removeFrom(this.map);
    }

    for (let i = 0; i < this.show; i++) {
      this.polylines[i].addTo(this.map);
      this.markers[i].addTo(this.map);
    }

    this.currentLocationMarker.addTo(this.map);
  }
}
