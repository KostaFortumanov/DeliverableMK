import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DefaultComponent } from './layouts/default/default.component';
import { LoginComponent } from './layouts/login/login.component';

import { AddjobComponent } from './modules/addjob/addjob.component';
import { AllDriversComponent } from './modules/all-drivers/all-drivers.component';
import { DashboardComponent } from './modules/dashboard/dashboard.component';
import { DriverMapComponent } from './modules/driver-map/driver-map.component';
import { LoginFormComponent } from './modules/login-form/login-form.component';
import { SelectDriversComponent } from './modules/select-drivers/select-drivers.component';
import { AuthGuard } from './helpers';

const routes: Routes = [
  {
    path: '',
    component: DefaultComponent,
    // canActivate: [AuthGuard],
    children: [
      {
        path: '',
        component: DashboardComponent,
      },
      {
        path: 'map',
        component: DriverMapComponent,
      },
      {
        path: 'addJob',
        component: AddjobComponent,
      },
      {
        path: 'allDrivers',
        component: AllDriversComponent,
      },
      {
        path: 'selectDrivers',
        component: SelectDriversComponent,
      },
    ],
  },
  {
    path: 'login',
    component: LoginComponent,
    children: [
      {
        path: '',
        component: LoginFormComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
