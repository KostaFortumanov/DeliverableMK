import { AfterViewInit, Component } from '@angular/core';
import * as L from 'leaflet';
import { DriverMapService } from 'src/app/services/driver-map.service';

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

  private initMap(): void {
    this.map = L.map('driverMap', {
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
    this.paths = driverMapService.getPaths();
  }

  ngAfterViewInit(): void {
    this.drawMap();
  }

  drawMap() {
    this.initMap();
    this.tiles.addTo(this.map);
    let marker = L.marker(L.GeoJSON.coordsToLatLng([21.4607121, 41.9896275]));
    marker.addTo(this.map);
    for (let [i, path] of this.paths.entries()) {
      let polyline = L.polyline(L.GeoJSON.coordsToLatLngs(path), {
        color: 'red',
      });
      polyline.addTo(this.map);
      marker = L.marker(L.GeoJSON.coordsToLatLng(path[path.length - 1]));
      marker.bindPopup(`Пратка ${i + 1}`).openPopup();
      marker.addTo(this.map);
    }
  }
}
