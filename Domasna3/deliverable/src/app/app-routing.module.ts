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
import { RegisterFormComponent } from './modules/register-form/register-form.component';
import { NewAccountComponent } from './modules/new-account/new-account.component';
import { ManagerJobsComponent } from './modules/manager-jobs/manager-jobs.component';
import { ManagerMapComponent } from './modules/manager-map/manager-map.component';

const routes: Routes = [
  {
    path: '',
    component: DefaultComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        component: DashboardComponent,
      },
      {
        path: 'map',
        component: DriverMapComponent,
        canActivate: [AuthGuard],
        data: { role: 'DRIVER'}
      },
      {
        path: 'managerMap',
        component: ManagerMapComponent,
        canActivate: [AuthGuard],
        data: { role: 'MANAGER'}
      },
      {
        path: 'addJob',
        component: AddjobComponent,
        canActivate: [AuthGuard],
        data: { role: 'MANAGER'}
      },
      {
        path: 'allDrivers',
        component: AllDriversComponent,
        canActivate: [AuthGuard],
        data: { role: 'MANAGER'}
      },
      {
        path: 'selectDrivers',
        component: SelectDriversComponent,
        canActivate: [AuthGuard],
        data: { role: 'MANAGER'}
      },
      {
        path: 'jobs',
        component: ManagerJobsComponent,
        canActivate: [AuthGuard],
        data: { role: 'MANAGER'}
      }
    ],
  },
  {
    path: '',
    component: LoginComponent,
    children: [
      {
        path: 'login',
        component: LoginFormComponent,
      },
      {
        path: 'register',
        component: RegisterFormComponent,
        canActivate: [AuthGuard],
        data: { role: 'MANAGER'}
      },
      {
        path: 'newAccount',
        component: NewAccountComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
