import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { JhvitaClienteModule } from './cliente/cliente.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        JhvitaClienteModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class JhvitaEntityModule {}
