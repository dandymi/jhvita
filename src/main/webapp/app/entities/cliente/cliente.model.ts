export interface ICliente {
  id?: number;
  codCliente?: number | null;
  ragioneSociale?: string;
  partitaIVA?: string | null;
}

export class Cliente implements ICliente {
  constructor(public id?: number, public codCliente?: number | null, public ragioneSociale?: string, public partitaIVA?: string | null) {}
}

export function getClienteIdentifier(cliente: ICliente): number | undefined {
  return cliente.id;
}
