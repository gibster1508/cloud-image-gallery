import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule, Routes} from "@angular/router";
import {AccessDeniedComponent} from "./components/access-denied/access-denied.component";
import {AuthGuard} from "./auth/auth.guard";
import {UserInfoComponent} from "./components/user-info/user-info.component";
import {MAT_SNACK_BAR_DEFAULT_OPTIONS} from "@angular/material/snack-bar";
import {ImageGalleryComponent} from "./components/image-gallery/image-gallery.component";
import {PrivateImageGalleryComponent} from "./components/private-image-gallery/private-image-gallery.component";

const routes: Routes = [
  {
    path: 'access-denied',
    component: AccessDeniedComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'user-info',
    component: UserInfoComponent,
    canActivate: [AuthGuard],
    data: {roles: ['user']}
  },
  {
    path: '',
    component: ImageGalleryComponent,
    canActivate: [AuthGuard],
    data: {roles: ['user']}
  },
  {
    path: 'private-gallery',
    component: PrivateImageGalleryComponent,
    canActivate: [AuthGuard],
    data: {roles: ['user']}
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes), CommonModule],
  exports: [RouterModule],
  providers: [    {
    provide: MAT_SNACK_BAR_DEFAULT_OPTIONS,
    useValue: { duration: 6000, horizontalPosition: 'right', verticalPosition: 'bottom', backGroundColor: '3f51b5'}
  }]
})
export class AppRoutingModule {
}
