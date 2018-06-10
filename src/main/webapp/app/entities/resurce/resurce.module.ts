import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { GumehSharedModule } from '../../shared';
import {
    ResurceService,
    ResurcePopupService,
    ResurceComponent,
    ResurceDetailComponent,
    ResurceDialogComponent,
    ResurcePopupComponent,
    ResurceDeletePopupComponent,
    ResurceDeleteDialogComponent,
    resurceRoute,
    resurcePopupRoute,
    ResurceResolvePagingParams,
} from './';

const ENTITY_STATES = [
    ...resurceRoute,
    ...resurcePopupRoute,
];

@NgModule({
    imports: [
        GumehSharedModule,
        RouterModule.forChild(ENTITY_STATES)
    ],
    declarations: [
        ResurceComponent,
        ResurceDetailComponent,
        ResurceDialogComponent,
        ResurceDeleteDialogComponent,
        ResurcePopupComponent,
        ResurceDeletePopupComponent,
    ],
    entryComponents: [
        ResurceComponent,
        ResurceDialogComponent,
        ResurcePopupComponent,
        ResurceDeleteDialogComponent,
        ResurceDeletePopupComponent,
    ],
    providers: [
        ResurceService,
        ResurcePopupService,
        ResurceResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GumehResurceModule {}
