import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { GumehSharedModule } from '../../shared';
import { GumehAdminModule } from '../../admin/admin.module';
import {
    FollowershipService,
    FollowershipPopupService,
    FollowershipComponent,
    FollowershipDetailComponent,
    FollowershipDialogComponent,
    FollowershipPopupComponent,
    FollowershipDeletePopupComponent,
    FollowershipDeleteDialogComponent,
    followershipRoute,
    followershipPopupRoute,
    FollowershipResolvePagingParams,
} from './';

const ENTITY_STATES = [
    ...followershipRoute,
    ...followershipPopupRoute,
];

@NgModule({
    imports: [
        GumehSharedModule,
        GumehAdminModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        FollowershipComponent,
        FollowershipDetailComponent,
        FollowershipDialogComponent,
        FollowershipDeleteDialogComponent,
        FollowershipPopupComponent,
        FollowershipDeletePopupComponent,
    ],
    entryComponents: [
        FollowershipComponent,
        FollowershipDialogComponent,
        FollowershipPopupComponent,
        FollowershipDeleteDialogComponent,
        FollowershipDeletePopupComponent,
    ],
    providers: [
        FollowershipService,
        FollowershipPopupService,
        FollowershipResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GumehFollowershipModule {}
