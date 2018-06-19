import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { GumehSharedModule } from '../../shared';
import { GumehAdminModule } from '../../admin/admin.module';
import {
    ActivityService,
    ActivityPopupService,
    ActivityComponent,
    ActivityDetailComponent,
    ActivityDialogComponent,
    ActivityPopupComponent,
    ActivityDeletePopupComponent,
    ActivityDeleteDialogComponent,
    activityRoute,
    activityPopupRoute,
    ActivityResolvePagingParams,
} from './';

const ENTITY_STATES = [
    ...activityRoute,
    ...activityPopupRoute,
];

@NgModule({
    imports: [
        GumehSharedModule,
        GumehAdminModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        ActivityComponent,
        ActivityDetailComponent,
        ActivityDialogComponent,
        ActivityDeleteDialogComponent,
        ActivityPopupComponent,
        ActivityDeletePopupComponent,
    ],
    entryComponents: [
        ActivityComponent,
        ActivityDialogComponent,
        ActivityPopupComponent,
        ActivityDeleteDialogComponent,
        ActivityDeletePopupComponent,
    ],
    providers: [
        ActivityService,
        ActivityPopupService,
        ActivityResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GumehActivityModule {}
