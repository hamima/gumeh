import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil } from 'ng-jhipster';

import { UserRouteAccessService } from '../../shared';
import { FollowershipComponent } from './followership.component';
import { FollowershipDetailComponent } from './followership-detail.component';
import { FollowershipPopupComponent } from './followership-dialog.component';
import { FollowershipDeletePopupComponent } from './followership-delete-dialog.component';

@Injectable()
export class FollowershipResolvePagingParams implements Resolve<any> {

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

export const followershipRoute: Routes = [
    {
        path: 'followership',
        component: FollowershipComponent,
        resolve: {
            'pagingParams': FollowershipResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'gumehApp.followership.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'followership/:id',
        component: FollowershipDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'gumehApp.followership.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const followershipPopupRoute: Routes = [
    {
        path: 'followership-new',
        component: FollowershipPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'gumehApp.followership.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'followership/:id/edit',
        component: FollowershipPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'gumehApp.followership.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'followership/:id/delete',
        component: FollowershipDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'gumehApp.followership.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
