import { AfterViewInit, Component } from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-fullscreen';
import { DriverMapService } from 'src/app/services/driver-map.service';

export interface Job {
  id: number;
  address: string;
  description: string;
}

@Component({
  selector: 'app-driver-map',
  templateUrl: './driver-map.component.html',
  styleUrls: ['./driver-map.component.scss'],
})
export class DriverMapComponent implements AfterViewInit {
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
  currentLocationMarker: L.Marker = L.marker(
    L.GeoJSON.coordsToLatLng([21.4443826, 41.994568]),
    { icon: this.myLocationIcon }
  );

  private initMap(): void {
    this.map =  L.map('driverMap', {
      fullscreenControl: true,
      center: [41.9961, 21.4316],
      zoom: 13,
    });
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

  constructor(private driverMapService: DriverMapService) {
    driverMapService.getPaths().subscribe((data) => {
      this.paths = data;

      this.show = this.paths.length;
      console.log(this.show);
      this.init();
      this.drawMap();
    });
  }

  ngAfterViewInit(): void {
    this.initMap();
    this.tiles.addTo(this.map);
  }

  init() {
    for (let i = 0; i < this.paths.length; i++) {
      this.polylines.push(
        L.polyline(L.GeoJSON.coordsToLatLngs(this.paths[i]), { color: 'red' })
      );

      let icon = L.icon({
        iconUrl: `./assets/markerIcons/number_${i + 1}.png`,
        iconSize: [30, 30],
      });

      let marker = L.marker(
        L.GeoJSON.coordsToLatLng(this.paths[i][this.paths[i].length - 1]),
        { icon: icon }
      );
      this.markers.push(marker);
    }


    this.getJobInfo();
    this.drawMap();
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

  finishJob(id: number) {
    console.log(id);
  }

  drawMap() {
    if (this.show > this.paths.length) {
      this.show = this.paths.length;
    }

    if (this.show < 1) {
      this.show = 1;
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
