import { BaseEntity } from './../../shared';

export class Cliente implements BaseEntity {
    constructor(
        public id?: number,
        public codCliente?: number,
        public ragioneSociale?: string,
        public partitaIVA?: string,
    ) {
    }
}
