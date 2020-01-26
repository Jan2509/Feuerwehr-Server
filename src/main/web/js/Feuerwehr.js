/* Styles */
import '../css/entry.scss'
/* fontawesome */
import '@fortawesome/fontawesome-free/js/fontawesome'
import '@fortawesome/fontawesome-free/js/solid'
import '@fortawesome/fontawesome-free/js/regular'
import '@fortawesome/fontawesome-free/js/brands'

/* Scripts */
import {startLogin} from "./Feuerwehr-login";
import {handleLogout} from "./Feuerwehr-logout";
import {initOuterPage} from "./outer-page";
import {startCreate} from "./Feuerwehr-User";
import {einsatzCreate} from "./Feuerwehr-Einsatz";
import {addCreate} from "./Feuerwehr-add";

window.loginPage = function () {
    startLogin()
};
window.onLogoutInput = function () {
    handleLogout()
};
window.onOuterPageFinish = function () {
    initOuterPage()
};
window.createPage = function () {
    startCreate()
};
window.createEinsatzPage = function () {
    einsatzCreate()
};
window.addPage = function () {
    addCreate()
};