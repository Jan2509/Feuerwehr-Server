import $ from 'jquery'
import Swal from "sweetalert2";
import {addJson} from "./json-components.js";

function addCreate() {

    $('#create-form').submit(function (event) {
        event.preventDefault();
        let usernameField = $('#create-name-input');
        let bezeichnungField = $('#create-aus-input');
        let loginBTM = $('#create-btn')
        let username = usernameField.val();
        let bezeichnung = bezeichnungField.val();
        requestCreate(username, bezeichnung, {
            btn: loginBTM,
            usernameField: usernameField,
            bezeichnungField: bezeichnungField
        });
    })

}

function requestCreate(username, bezeichnung, fields) {
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
    disableCreateForm(fields);


    const request = new XMLHttpRequest();
    request.addEventListener('load', function (event) {
        if (request.status >= 200 && request.status < 300) {
            handleCreateReponse(JSON.parse(request.responseText), fields);
        } else {
            console.warn(request.statusText, request.responseText);
            enableCreateForm(fields);
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
    request.open("POST", "/api/internal/add", true);
    request.setRequestHeader("Content-Type", "application/json");
    request.send(JSON.stringify(new addJson(username, bezeichnung)))
}

function handleCreateReponse(response, fields) {
    //Swal.close();
    enableCreateForm(fields);
    if (response.success) {
        Swal.fire({
            title: 'You have succesfully added an Ausbildung!',
            icon: "success",
            confirmButtonText: 'Close',
            onAfterClose() {
                window.location.replace("/Ausbildung")
            },
            showCancelButton: false,
            showCloseButton: false,
            showConfirmButton: true,
            keydownListenerCapture: false,
            allowOutsideClick: false,
        });
        window.location.replace("/Ausbildung")
    } else {

        Swal.fire({
            title: 'Oops something went wrong!',
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

function disableCreateForm(fields) {
    fields.usernameField.attr("disabled", true);
    fields.bezeichnungField.attr("disabled", true);
    fields.btn.attr("disabled", true);
    fields.btn.addClass("is-loading");
    fields.btn.removeClass("is-light");

}

function enableCreateForm(fields) {
    fields.usernameField.attr("disabled", false);
    fields.bezeichnungField.attr("disabled", false);
    fields.btn.attr("disabled", false);
    fields.btn.removeClass("is-loading");
    fields.btn.addClass("is-light");

}

export {addCreate}