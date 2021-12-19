import { Component, OnInit, OnDestroy } from '@angular/core';
import * as L from 'leaflet';
import { environment } from 'src/environments/environment';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { DriverMapService } from 'src/app/services/driver-map.service';
import { TokenStorageService } from 'src/app/services/token-storage.service';

export interface Driver {
  id: number;
  name: string;
  currentLon: number;
  currentLat: number;
  destinationLon: number;
  destinationLat: number;
  job: Job;
  lastUpdate: number;
  seconds: number;
}

export interface Job {
  id: number;
  address: string;
  description: string;
}

const webSocketEndPoint = environment.wsEndPoint;

@Component({
  selector: 'app-manager-map',
  templateUrl: './manager-map.component.html',
  styleUrls: ['./manager-map.component.scss'],
})
export class ManagerMapComponent implements OnInit, OnDestroy {
  interval!: any;

  constructor(
    private driverMapService: DriverMapService,
    private tokenStorageService: TokenStorageService
  ) {
    if (tokenStorageService.getUser().role === 'MANAGER') {
      this.connect();
      this.interval = setInterval(() => {
        for (let i = 0; i < this.drivers.length; i++) {
          let diff = Math.floor(
            (Date.now() - this.drivers[i].lastUpdate) / 1000
          );
          console.log(Date.now() + ' ' + this.drivers[i].lastUpdate);
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
  drivers: Driver[] = [];
  polylines: L.Polyline[] = [];
  markersStart: L.Marker[] = [];
  markersEnd: L.Marker[] = [];

  managerMap!: L.Map;
  stompClient2!: any;

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
    console.log('Initialize WebSocket Connection');
    let ws = new SockJS(webSocketEndPoint);
    this.stompClient2 = Stomp.over(ws);

    this.stompClient2.connect({}, () => {
      this.stompClient2.subscribe('/user/managerMap/queue/messages', (message: any) => {
        message = JSON.parse(message.body);
        console.log(message);
        let exists = false;
        for (let i = 0; i < this.drivers.length; i++) {
          console.log(this.drivers[i].id + ' ' + message.id);
          if (this.drivers[i].id === message.id) {
            console.log(i);
            exists = true;
            this.drivers[i] = message;
            this.drivers[i].lastUpdate = Date.now();
            console.log(this.drivers[i]);
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
                  L.GeoJSON.coordsToLatLngs(data),
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
          // message.lastUpdate = Date.now();
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
              let polyline = L.polyline(L.GeoJSON.coordsToLatLngs(data), {
                color: 'red',
              });
              this.polylines.push(polyline);
              // polyline.addTo(this.managerMap);
            });

          let icon = L.icon({
            iconUrl: `./assets/markerIcons/myLocation.png`,
            iconSize: [30, 30],
          });
          let marker = L.marker([message.currentLat, message.currentLon], {
            icon: icon,
          }).bindPopup(`<p>${message.name}</p>`);
          this.markersStart.push(marker);
          // marker.addTo(this.managerMap);

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
          // marker.addTo(this.managerMap);
          // this.selected.push(this.drivers[this.drivers.length - 1].id);
        }
      });
      //_this.stompClient.reconnect_delay = 2000;
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
      this.stompClient2.disconnect();
  }
}
