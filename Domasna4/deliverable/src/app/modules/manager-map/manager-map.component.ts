import { Component, OnInit, OnDestroy } from '@angular/core';
import * as L from 'leaflet';
import { DriverMapService } from 'src/app/services/driver-map.service';
import { TokenStorageService } from 'src/app/services/token-storage.service';
import { ManagerMapDriver } from 'src/app/models/manager-map-driver';
import { WebSocketService } from 'src/app/services/web-socket.service';

@Component({
  selector: 'app-manager-map',
  templateUrl: './manager-map.component.html',
  styleUrls: ['./manager-map.component.scss'],
})
export class ManagerMapComponent implements OnInit, OnDestroy {
  interval!: any;

  constructor(
    private driverMapService: DriverMapService,
    private tokenStorageService: TokenStorageService,
    private webSocketService: WebSocketService
  ) {
    if (tokenStorageService.getUser().role === 'MANAGER') {
      this.connect();
      this.interval = setInterval(() => {
        for (let i = 0; i < this.drivers.length; i++) {
          let diff = Math.floor(
            (Date.now() - this.drivers[i].lastUpdate) / 1000
          );
          this.drivers[i].seconds = diff;
        }
      }, 1000);
    }
  }

  getDrivers() {
    return this.drivers;
  }

  drawMap() {
    for (let i = 0; i < this.drivers.length; i++) {
      this.polylines[i].removeFrom(this.managerMap);
      this.markersStart[i].removeFrom(this.managerMap);
      this.markersEnd[i].removeFrom(this.managerMap);
    }

    for (let i = 0; i < this.drivers.length; i++) {
      if (this.driverSelected(this.drivers[i].id)) {
        this.polylines[i].addTo(this.managerMap);
        this.markersStart[i].addTo(this.managerMap);
        this.markersEnd[i].addTo(this.managerMap);
      }
    }
  }

  driverSelected(id: number) {
    return this.selected.includes(id);
  }

  selected: number[] = [];
  drivers: ManagerMapDriver[] = [];
  polylines: L.Polyline[] = [];
  markersStart: L.Marker[] = [];
  markersEnd: L.Marker[] = [];

  managerMap!: L.Map;

  private tiles = L.tileLayer(
    'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
    {
      maxZoom: 18,
      minZoom: 3,
      attribution:
        '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    }
  );

  ngOnInit(): void {
    this.initMap();
    this.tiles.addTo(this.managerMap);
  }

  connect() {
    this.webSocketService.getStompClient().then((stopmclient) => {
      stopmclient.subscribe(
        '/user/managerMap/queue/messages',
        (message: any) => {
          message = JSON.parse(message.body);
          let exists = false;
          for (let i = 0; i < this.drivers.length; i++) {
            if (this.drivers[i].id === message.id) {
              exists = true;
              this.drivers[i] = message;
              this.drivers[i].lastUpdate = Date.now();
              this.markersStart[i].removeFrom(this.managerMap);
              this.markersEnd[i].removeFrom(this.managerMap);
              this.driverMapService
                .updateCurrentPath(
                  this.drivers[i].currentLon,
                  this.drivers[i].currentLat,
                  this.drivers[i].destinationLon,
                  this.drivers[i].destinationLat
                )
                .subscribe((data) => {
                  this.polylines[i].removeFrom(this.managerMap);
                  this.polylines[i] = L.polyline(
                    L.GeoJSON.coordsToLatLngs(data.path),
                    {
                      color: 'red',
                    }
                  );
                  if (this.driverSelected(this.drivers[i].id)) {
                    this.polylines[i].addTo(this.managerMap);
                  }
                });

              let icon = L.icon({
                iconUrl: `./assets/markerIcons/myLocation.png`,
                iconSize: [30, 30],
              });
              this.markersStart[i] = L.marker(
                [this.drivers[i].currentLat, this.drivers[i].currentLon],
                { icon: icon }
              ).bindPopup(`<p>${this.drivers[i].name}</p>`);

              if (!this.drivers[i].job) {
                icon = L.icon({
                  iconUrl: `./assets/markerIcons/office.png`,
                  iconSize: [30, 30],
                });
                this.markersEnd[i] = L.marker(
                  [
                    this.drivers[i].destinationLat,
                    this.drivers[i].destinationLon,
                  ],
                  { icon: icon }
                );
              } else {
                this.markersEnd[i] = L.marker([
                  this.drivers[i].destinationLat,
                  this.drivers[i].destinationLon,
                ]).bindPopup(
                  `<p>${this.drivers[i].job.id}</p><p>${this.drivers[i].job.address}</p><p>${this.drivers[i].job.description}</p>`
                );
              }

              if (this.driverSelected(this.drivers[i].id)) {
                this.polylines[i].addTo(this.managerMap);
                this.markersStart[i].addTo(this.managerMap);
                this.markersEnd[i].addTo(this.managerMap);
              }
            }
          }

          if (!exists) {
            this.drivers.push(message);
            this.drivers[this.drivers.length - 1].lastUpdate = Date.now();
            this.driverMapService
              .updateCurrentPath(
                message.currentLon,
                message.currentLat,
                message.destinationLon,
                message.destinationLat
              )
              .subscribe((data) => {
                let polyline = L.polyline(
                  L.GeoJSON.coordsToLatLngs(data.path),
                  {
                    color: 'red',
                  }
                );
                this.polylines.push(polyline);
              });

            let icon = L.icon({
              iconUrl: `./assets/markerIcons/myLocation.png`,
              iconSize: [30, 30],
            });
            let marker = L.marker([message.currentLat, message.currentLon], {
              icon: icon,
            }).bindPopup(`<p>${message.name}</p>`);
            this.markersStart.push(marker);

            if (!message.job) {
              icon = L.icon({
                iconUrl: `./assets/markerIcons/office.png`,
                iconSize: [30, 30],
              });
              marker = L.marker(
                [message.destinationLat, message.destinationLon],
                { icon: icon }
              );
            } else {
              marker = L.marker([
                message.destinationLat,
                message.destinationLon,
              ]).bindPopup(
                `<p>${message.job.id}</p><p>${message.job.address}</p><p>${message.job.description}</p>`
              );
            }

            this.markersEnd.push(marker);
          }
        },
        { id: 'managerMapTopic' }
      );
    });
  }

  private initMap(): void {
    this.managerMap = L.map('managerMap', {
      fullscreenControl: true,
      center: [41.9961, 21.4316],
      zoom: 13,
    });
  }

  ngOnDestroy(): void {
    clearInterval(this.interval);
    this.webSocketService.getStompClient().then((stopmclient) => {
      stopmclient.unsubscribe('managerMapTopic');
    });
  }
}
