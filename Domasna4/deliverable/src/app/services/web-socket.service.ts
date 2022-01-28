import { Injectable } from '@angular/core';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { environment } from 'src/environments/environment';

const webSocketEndPoint = environment.wsEndPoint;

@Injectable({
  providedIn: 'root',
})
export class WebSocketService {
  stompClient: any;
  connected = false;

  constructor() {}

  async getStompClient() {
    if (!this.connected) {
      await this.connect();
    }

    return this.stompClient;
  }

  connect() {
    let ws = new SockJS(webSocketEndPoint);
    this.stompClient = Stomp.over(ws);
    this.stompClient.debug = () => {};
    this.stompClient.connect({}, () => {this.connected=true}, this.errorCallBack);
    return new Promise(resolve => setTimeout(resolve, 2000));
  }

  errorCallBack(error: string) {
    setTimeout(() => {
      this.connect();
    }, 5000);
  }
}
