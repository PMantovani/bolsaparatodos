import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiClientService {
  baseApiUrl = environment.apiBaseUrl;
  headers: HttpHeaders;

  constructor(private http: HttpClient) {
    this.headers = new HttpHeaders({
      authorization : 'Basic ' + btoa('pedro@email.com' + ':' + '1234'),
    });
  }

  get(endpoint: string, params?: HttpParams): Observable<any> {
    return this.http.get(this.baseApiUrl + endpoint, {headers: this.headers, params});
  }
}
