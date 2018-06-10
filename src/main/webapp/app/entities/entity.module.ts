import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { GumehResurceModule } from './resurce/resurce.module';
import { GumehTagModule } from './tag/tag.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        GumehResurceModule,
        GumehTagModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GumehEntityModule {}
