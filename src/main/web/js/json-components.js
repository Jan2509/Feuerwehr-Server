class RequestJson {

    constructor(username, password) {
        this.username = username;
        this.password = password;
    }
}

export {RequestJson}
class CreateRequestJson {

    constructor(username, vorname, password, tel, geb, ein) {
        this.username = username;
        this.vorname = vorname;
        this.password = password;
        this.tel = tel;
        this.geb = geb;
        this.ein = ein;
    }
}

export {CreateRequestJson}
class addJson {

    constructor(username, bezeichnung) {
        this.username = username;
        this.bezeichnung = bezeichnung
    }
}

export {addJson}

