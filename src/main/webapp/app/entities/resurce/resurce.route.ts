import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil } from 'ng-jhipster';

import { UserRouteAccessService } from '../../shared';
import { ResurceComponent } from './resurce.component';
import { ResurceDetailComponent } from './resurce-detail.component';
import { ResurcePopupComponent } from './resurce-dialog.component';
import { ResurceDeletePopupComponent } from './resurce-delete-dialog.component';

@Injectable()
export class ResurceResolvePagingParams implements Resolve<any> {

    constructor(private paginationUtil: JhiPaginationUtil) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const page = route.queryParams['page'] ? route.queryParams['page'] : '1';
        const sort = route.queryParams['sort'] ? route.queryParams['sort'] : 'id,asc';
        return {
            page: this.paginationUtil.parsePage(page),
            predicate: this.paginationUtil.parsePredicate(sort),
            ascending: this.paginationUtil.parseAscending(sort)
      };
    }
}

export const resurceRoute: Routes = [
    {
        path: 'resurce',
        component: ResurceComponent,
        resolve: {
            'pagingParams': ResurceResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'gumehApp.resurce.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'resurce/:id',
        component: ResurceDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'gumehApp.resurce.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const resurcePopupRoute: Routes = [
    {
        path: 'resurce-new',
        component: ResurcePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'gumehApp.resurce.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'resurce/:id/edit',
        component: ResurcePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'gumehApp.resurce.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'resurce/:id/delete',
        component: ResurceDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'gumehApp.resurce.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
