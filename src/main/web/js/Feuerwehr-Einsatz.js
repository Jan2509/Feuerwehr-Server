import $ from 'jquery'
import Swal from "sweetalert2";
import {EinsatzRequestJson} from "./einsatz-components.js";

function einsatzCreate() {

    $('#create-einsatz-form').submit(function (event) {
        event.preventDefault();
        let stichwortField = $('#create-stichwort-input');
        let strasseField = $('#create-strasse-input');
        let hausnrField = $('#create-hausnr-input');
        let plzField = $('#create-plz-input');
        let ortField = $('#create-ort-input');
        let bemerkungenField = $('#create-bemerkungen-input');
        let createBTn = $('#create-btn');

        let stichwort = stichwortField.val();
        let strasse = strasseField.val();
        let hausnr = hausnrField.val();
        let plz = plzField.val();
        let ort = ortField.val();
        let bemerkungen = bemerkungenField.val();


        requestEinsatzCreate(stichwort, strasse,hausnr, plz, ort,bemerkungen,  {
            btn: createBTn,
            stichwortField: stichwortField,
            strasseField: strasseField,
            hausnrField: hausnrField,
            plzField: plzField,
            ortField: ortField,
            bemerkungenField:bemerkungenField
        });
    })

}

function requestEinsatzCreate(stichwort, strasse, hausnr, plz, ort, bemerkungen, fields) {
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
    request.open("POST", "/api/internal/createEinsatz", true);
    request.setRequestHeader("Content-Type", "application/json");
    request.send(JSON.stringify(new EinsatzRequestJson(stichwort, strasse, hausnr, plz, ort,bemerkungen)))
}

function handleCreateReponse(response, fields) {
    //Swal.close();
    enableCreateForm(fields);
    if (response.success) {
        Swal.fire({
            title: 'You have succesfully created an Alarm!',
            icon: "success",
            confirmButtonText: 'Close',
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
    fields.stichwortField.attr("disabled", true);
    fields.strasseField.attr("disabled", true);
    fields.hausnrField.attr("disabled", true);
    fields.plzField.attr("disabled", true);
    fields.ortField.attr("disabled", true);
    fields.bemerkungenField.attr("disabled", true);
    fields.btn.attr("disabled", true);
    fields.btn.addClass("is-loading");
    fields.btn.removeClass("is-light");

}

function enableCreateForm(fields) {
    fields.stichwortField.attr("disabled", false);
    fields.strasseField.attr("disabled", false);
    fields.hausnrField.attr("disabled", false);
    fields.plzField.attr("disabled", false);
    fields.ortField.attr("disabled", false);
    fields.bemerkungenField.attr("disabled", false);
    fields.btn.attr("disabled", false);
    fields.btn.removeClass("is-loading");
    fields.btn.addClass("is-light");

}

export {einsatzCreate}