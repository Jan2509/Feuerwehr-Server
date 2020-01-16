class EinsatzRequestJson {

    constructor(stichwort, strasse, hausnr, plz, ort, bemerkungen) {
        this.stichwort = stichwort;
        this.strasse = strasse;
        this.hausnr = hausnr;
        this.plz = plz;
        this.ort = ort;
        this.bemerkungen = bemerkungen;
    }
}

export {EinsatzRequestJson}