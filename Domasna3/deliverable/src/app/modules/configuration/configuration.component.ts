import { Component, OnInit } from '@angular/core';
import * as L from 'leaflet';
import { finalize } from 'rxjs/operators';
import { ConfigurationService } from 'src/app/services/configuration.service';

@Component({
  selector: 'app-configuration',
  templateUrl: './configuration.component.html',
  styleUrls: ['./configuration.component.scss'],
})
export class ConfigurationComponent implements OnInit {
  show = false;
  pageLoading = true;
  error = '';

  constructor(private configService: ConfigurationService) {
    configService
      .getConfig()
      .pipe(
        finalize(() => {
          this.pageLoading = false;
          this.show = true;
	  setTimeout(() => {
	     this.initMap();
             this.tiles.addTo(this.configMap);
             this.marker.addTo(this.configMap);
	  }, 100);
        })
      )
      .subscribe(
        (data) => {
          this.startTime = data.startTime;
          this.endTime = data.endTime;
          this.chooseLocationLat = data.startLat;
          this.chooseLocationLon = data.startLon;
          this.marker = L.marker(
            [this.chooseLocationLat, this.chooseLocationLon],
            { icon: this.icon }
          );
        },
        (error) => {
          this.error = 'Server unavailable';
        }
      );
  }

  ngOnInit(): void {
    
  }

  loading = false;
  success = '';
  startTime!: any;
  endTime!: any;
  configMap!: L.Map;
  marker = L.marker(L.GeoJSON.coordsToLatLng([0, 0]));
  chooseLocationLat: number = 0;
  chooseLocationLon: number = 0;
  icon = L.icon({
    iconUrl: `./assets/markerIcons/office.png`,
    iconSize: [30, 30],
  });

  private initMap(): void {
    this.configMap = L.map('configMap', {
      center: [41.9961, 21.4316],
      zoom: 13,
    });

    this.configMap.on('click', <LeafletMouseEvent>(e: { latlng: any }) => {
      this.marker.removeFrom(this.configMap);
      this.marker = L.marker(e.latlng, { icon: this.icon });
      this.marker.addTo(this.configMap);
      this.chooseLocationLat = e.latlng.lat;
      this.chooseLocationLon = e.latlng.lng;
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

  changeSettings() {
    this.loading = true;
    this.configService
      .saveConfig(
        this.startTime,
        this.endTime,
        this.chooseLocationLat,
        this.chooseLocationLon
      )
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe((data) => {
        this.success = 'Changed settings';
      });
  }
}
