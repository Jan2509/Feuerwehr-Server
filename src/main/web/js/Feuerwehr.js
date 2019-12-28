
import {startLogin} from "./Feuerwehr-login";
import {handleLogout} from "./Feuerwehr-logout";

window.loginPage = function () {
    startLogin()
};
window.onLogoutInput = function () {
    handleLogout()
};