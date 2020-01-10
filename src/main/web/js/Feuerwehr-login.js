import $ from 'jquery'
import Swal from "sweetalert2";
import {LoginRequestJson} from "./json-components.js";

function startLogin() {

    $('#login-form').submit(function (event) {
        event.preventDefault();
        let usernameField = $('#username-input');
        let passwordField = $('#password-input');
        let loginBTM = $('#login-btn');
        let username = usernameField.val();
        let password = passwordField.val();
        requestLogin(username, password, {
            btn: loginBTM,
            usernameField: usernameField,
            passwordField: passwordField
        });
    })

}

async function initParticles() {

}

function requestLogin(username, password, fields) {
    Swal.fire({
        title: 'Please wait',
        showCancelButton: false,
        showCloseButton: false,
        showConfirmButton: false,
        keydownListenerCapture: false,
        allowOutsideClick: false,
        onOpen: function () {
            Swal.showLoading();
        }
    });
    disableLoginForm(fields);


    const request = new XMLHttpRequest();
    request.addEventListener('load', function (event) {
        if (request.status >= 200 && request.status < 300) {
            handleLoginReponse(JSON.parse(request.responseText), fields);
        } else {
            console.warn(request.statusText, request.responseText);
            enableLoginForm(fields);
            Swal.fire({
                title: 'Oops something went wrong!',
                icon: "warning",
                confirmButtonText: 'Reload Page',
                showCancelButton: false,
                showCloseButton: false,
                showConfirmButton: true,
                keydownListenerCapture: true,
                allowOutsideClick: false,
                onAfterClose() {
                    try {
                        window.parent.caches.delete("call")
                    } catch (e) {
                        console.log(e)
                    }
                    window.location.reload()
                },
            });
        }
    });
    request.open("POST", "/api/internal/login", true);
    request.setRequestHeader("Content-Type", "application/json");
    request.send(JSON.stringify(new LoginRequestJson(username, password)))
}

function handleLoginReponse(response, fields) {
    //Swal.close();
    enableLoginForm(fields);
    if (response.success) {
        Swal.fire({
            title: 'You are succesfully logged in!',
            icon: "success",
            confirmButtonText: 'to Dashboard',
            onAfterClose() {
                window.location.replace("/")
            },
            showCancelButton: false,
            showCloseButton: false,
            showConfirmButton: true,
            keydownListenerCapture: false,
            allowOutsideClick: false,
        });
        window.location.replace("/")
    } else {

        Swal.fire({
            title: 'username or password wrong!',
            icon: "error",
            confirmButtonText: 'Retry',
            showCancelButton: false,
            showCloseButton: true,
            showConfirmButton: true,
            keydownListenerCapture: true,
            allowOutsideClick: true,
        });
    }
}

function disableLoginForm(fields) {
    fields.usernameField.attr("disabled", true);
    fields.passwordField.attr("disabled", true);
    fields.btn.attr("disabled", true);
    fields.btn.addClass("is-loading");
    fields.btn.removeClass("is-light");

}

function enableLoginForm(fields) {
    fields.usernameField.attr("disabled", false);
    fields.passwordField.attr("disabled", false);
    fields.btn.attr("disabled", false);
    fields.btn.removeClass("is-loading");
    fields.btn.addClass("is-light");

}

export {startLogin}