import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
} from '@angular/common/http';
import { Observable } from 'rxjs';

import { TokenStorageService } from '../services/token-storage.service';
import { environment } from 'src/environments/environment';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private tokenStorageService: TokenStorageService) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    const currentUser = this.tokenStorageService.getUser();
    const isLoggedIn = currentUser && currentUser.token;
    const isApiUrl = request.url.startsWith(environment.apiUrl);
    if (isLoggedIn) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${currentUser.token}`,
        },
      });
    }

    return next.handle(request);
  }

  
}
