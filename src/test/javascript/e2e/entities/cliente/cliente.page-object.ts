import { element, by, ElementFinder } from 'protractor';

export class ClienteComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-cliente div table .btn-danger'));
  title = element.all(by.css('jhi-cliente div h2#page-heading span')).first();
  noResult = element(by.id('no-result'));
  entities = element(by.id('entities'));

  async clickOnCreateButton(): Promise<void> {
    await this.createButton.click();
  }

  async clickOnLastDeleteButton(): Promise<void> {
    await this.deleteButtons.last().click();
  }

  async countDeleteButtons(): Promise<number> {
    return this.deleteButtons.count();
  }

  async getTitle(): Promise<string> {
    return this.title.getAttribute('jhiTranslate');
  }
}

export class ClienteUpdatePage {
  pageTitle = element(by.id('jhi-cliente-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));

  idInput = element(by.id('field_id'));
  codClienteInput = element(by.id('field_codCliente'));
  ragioneSocialeInput = element(by.id('field_ragioneSociale'));
  partitaIVAInput = element(by.id('field_partitaIVA'));

  async getPageTitle(): Promise<string> {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  async setIdInput(id: string): Promise<void> {
    await this.idInput.sendKeys(id);
  }

  async getIdInput(): Promise<string> {
    return await this.idInput.getAttribute('value');
  }

  async setCodClienteInput(codCliente: string): Promise<void> {
    await this.codClienteInput.sendKeys(codCliente);
  }

  async getCodClienteInput(): Promise<string> {
    return await this.codClienteInput.getAttribute('value');
  }

  async setRagioneSocialeInput(ragioneSociale: string): Promise<void> {
    await this.ragioneSocialeInput.sendKeys(ragioneSociale);
  }

  async getRagioneSocialeInput(): Promise<string> {
    return await this.ragioneSocialeInput.getAttribute('value');
  }

  async setPartitaIVAInput(partitaIVA: string): Promise<void> {
    await this.partitaIVAInput.sendKeys(partitaIVA);
  }

  async getPartitaIVAInput(): Promise<string> {
    return await this.partitaIVAInput.getAttribute('value');
  }

  async save(): Promise<void> {
    await this.saveButton.click();
  }

  async cancel(): Promise<void> {
    await this.cancelButton.click();
  }

  getSaveButton(): ElementFinder {
    return this.saveButton;
  }
}

export class ClienteDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-cliente-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-cliente'));

  async getDialogTitle(): Promise<string> {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(): Promise<void> {
    await this.confirmButton.click();
  }
}
