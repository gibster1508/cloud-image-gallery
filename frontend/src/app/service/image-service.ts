import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {Injectable} from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class ImageService {

  constructor(private http: HttpClient) { }
  public getImages(params:any): Observable<any> {
    return this.http.get(`${environment.apiUrl}/images`, {params});
  }

  public searchImages(params:any): Observable<any> {
    return this.http.get(`${environment.apiUrl}/searchImages`, {params});
  }

  public uploadImages(uploadImageData:FormData): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/image/upload`,
      uploadImageData, { reportProgress: true, observe: 'events' });
  }

  public deleteImage(params:any): Observable<any> {
    return this.http.delete<any>(`${environment.apiUrl}/image/delete`, {params});
  }
}
