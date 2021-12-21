import { OnInit, Component, OnDestroy } from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-fullscreen';
import 'leaflet-easybutton';
import { DriverMapService } from 'src/app/services/driver-map.service';
import { finalize } from 'rxjs/operators';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { environment } from 'src/environments/environment';
import { TokenStorageService } from 'src/app/services/token-storage.service';

const webSocketEndPoint = environment.wsEndPoint;

export interface Job {
  id: number;
  address: string;
  description: string;
  order: number;
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
export class DriverMapComponent implements OnInit, OnDestroy {
  navigation!: any;
  track = false;
  map!: L.Map;
  paths!: any;
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
  info!: L.Control
  distance: string = '0';
  duration: number = 0;

  topic: string = '/user/' + 12 + '/queue/messages';
  stompClient: any;

  private initMap(): void {
    this.map = L.map('driverMap', {
      fullscreenControl: true,
      center: [41.9961, 21.4316],
      zoom: 13,
    });

    let myLocationButton = L.easyButton(
      '<i class="material-icons">my_location</i>',
      (btn, map) => {
        this.getLocation()
      },
      'Current location'
    ).addTo(this.map);

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
      },
      'Track movement'
    ).addTo(this.map);

    let dontTrackButton = L.easyButton(
      '<i class="material-icons">near_me</i>',
      (btn, map) => {
        this.track = false;
        dontTrackButton.remove();
        trackButton.addTo(this.map);
      },
      'Stop tracking movement'
    );

    this.info = new L.Control();

    this.info.onAdd = (map) => {
      let _div = L.DomUtil.create('div', 'info'); // create a div with a class "info"
      
      _div.innerHTML = this.duration == 0 && this.distance == '0' ? 'No info' :
        `
          <p><b>Duration: </b> ${this.duration < 60 ? '<1min' : new Date(this.duration * 1000).toISOString().substring(11, 16)+'min'}</p>
          <p><b>Distance: </b> ${this.distance}km</p>
        `
      return _div;
    };

    this.info.addTo(this.map);
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

  stompClient1!: any;

  constructor(
    private driverMapService: DriverMapService,
    private tokenStorageService: TokenStorageService
  ) {
    let ws = new SockJS(webSocketEndPoint);
    this.stompClient1 = Stomp.over(ws);
    this.stompClient1.connect({});
  }

  ngOnInit(): void {
    this.initMap();
    this.tiles.addTo(this.map);
    this.currentLocationMarker.addTo(this.map);

    let paths = window.sessionStorage.getItem('paths');

    if (paths) {
      this.paths = JSON.parse(paths);
      this.show = this.paths.length;
      console.log('if');
      this.getJobInfo();
    } else {
      this.driverMapService
        .getPaths()
        .pipe(
          finalize(() => {
            this.getJobInfo();
          })
        )
        .subscribe(
          (data) => {
            console.log('else');
            this.paths = data;
            this.show = this.paths.length;
            console.log(this.show);
            window.sessionStorage.setItem('paths', JSON.stringify(this.paths));
          },
          (error) => {}
        );
    }
  }

  init() {
    if (this.paths) {
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
            iconUrl: `./assets/markerIcons/number_${this.jobs[i].order}.png`,
            iconSize: [30, 30],
          });
        }

        let marker = L.marker(
          L.GeoJSON.coordsToLatLng(this.paths[i][this.paths[i].length - 1]),
          { icon: icon }
        );

        if (i != this.paths.length - 1) {
          marker.bindPopup(
            `<p>${this.jobs[i].id}</p><p>${this.jobs[i].address}</p><p>${this.jobs[i].description}</p>`
          );
        }

        this.markers.push(marker);
      }
    }

    

    this.drawMap();
  }

  currentJob = 0;

  getJobInfo() {
    this.driverMapService
      .getCurrentJobsInfo()
      .pipe(
        finalize(() => {
          this.init();
        })
      )
      .subscribe((data) => {
        this.jobs = data;
      });
  }

  updateCurrentPath(lon: number, lat: number) {
    if (this.paths) {
      let destination = this.paths[0][this.paths[0].length - 1];
      this.driverMapService
        .updateCurrentPath(lon, lat, destination[0], destination[1])
        .subscribe((data) => {
          this.distance = (data.distance / 1000).toFixed(2);
          this.duration = data.time;
          this.polylines[0].removeFrom(this.map);
          this.info.remove();
          this.info.addTo(this.map);
          this.polylines[0] = L.polyline(L.GeoJSON.coordsToLatLngs(data.path), {
            color: 'red',
          });
          this.polylines[0].addTo(this.map);

          let user = this.tokenStorageService.getUser();

          this.stompClient1.send(
            '/app/manager/map',
            {},
            JSON.stringify({
              id: user.id,
              name: user.firstName + ' ' + user.lastName,
              currentLon: lon,
              currentLat: lat,
              destinationLon: destination[0],
              destinationLat: destination[1],
              job: this.jobs[0],
            })
          );
        });
    }
  }

  finishJob() {
    this.driverMapService.finishJob(this.jobs[0].id).subscribe((data) => {
      let user = this.tokenStorageService.getUser();
      let message =
        user.firstName +
        ' ' +
        user.lastName +
        ' finished job ' +
        this.jobs[0].id;
      this.stompClient1.send(
        '/app/manager/finishJob',
        {},
        JSON.stringify(message)
      );

      this.jobs.shift();
      this.polylines[0].removeFrom(this.map);
      this.polylines.shift();
      this.markers[0].removeFrom(this.map);
      this.markers.shift();
      this.paths.shift();
      window.sessionStorage.removeItem('paths');
      if(this.jobs.length > 0) {
        window.sessionStorage.setItem('paths', JSON.stringify(this.paths));
      }

      this.drawMap();
      this.updateCurrentPath(
        this.currentLocationMarker.getLatLng().lng,
        this.currentLocationMarker.getLatLng().lat
      );
    });
  }

  drawMap() {
    if (this.show > this.polylines.length) {
      this.show = this.polylines.length;
    }

    if (this.show < 0) {
      this.show = 0;
    }

    // this.currentLocationMarker.removeFrom(this.map);

    for (let i = 0; i < this.polylines.length; i++) {
      this.polylines[i].removeFrom(this.map);
      this.markers[i].removeFrom(this.map);
    }

    for (let i = 0; i < this.show; i++) {
      this.polylines[i].addTo(this.map);
      this.markers[i].addTo(this.map);
    }

    // this.currentLocationMarker.addTo(this.map);
  }

  getLocation() {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        this.getLocationSucces(position)
        this.navigation = navigator.geolocation.watchPosition(
          (position) => {
            this.getLocationSucces(position);
          },
          (error) => {
            console.log('error');
          },
          options
        );
    }, () => {

    }, options)
  }

  getLocationSucces(position: any) {
    console.log('navigator');
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
      if (this.track) {
        this.map.panTo(new L.LatLng(lat, lon));
      }
      this.updateCurrentPath(lon, lat);
    }
  }

  ngOnDestroy() {
    navigator.geolocation.clearWatch(this.navigation);
    this.stompClient1.disconnect();
  }
}
