import {APP_INITIALIZER, NgModule} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import {initializer} from "../utils/app-init";
import {KeycloakAngularModule, KeycloakService} from "keycloak-angular";
import { AppRoutingModule } from './app-routing.module';
import { AccessDeniedComponent } from './components/access-denied/access-denied.component';
import { UserInfoComponent } from './components/user-info/user-info.component';
import {HttpClientModule} from "@angular/common/http";
import {AngularMaterialModule} from "./AngularMaterialModule";
import { NavBarComponent } from './components/nav-bar/nav-bar.component';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { ImageGalleryComponent } from './components/image-gallery/image-gallery.component';
import { ProgressBarComponent } from './components/progress-bar/progress-bar.component';
import {NgxDropzoneModule} from "ngx-dropzone";
import {NgxPaginationModule} from "ngx-pagination";
import {FormsModule} from "@angular/forms";

@NgModule({
  declarations: [
    AppComponent,
    AccessDeniedComponent,
    UserInfoComponent,
    NavBarComponent,
    ImageGalleryComponent,
    ProgressBarComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    KeycloakAngularModule,
    HttpClientModule,
    AngularMaterialModule,
    NgxDropzoneModule,
    NgxPaginationModule,
    FormsModule
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializer,
      deps: [ KeycloakService ],
      multi: true
    },
    provideAnimationsAsync()
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
