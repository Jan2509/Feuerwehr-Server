import Swal from "sweetalert2";

function handleLogout() {
    console.log("Logging out...");
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
    const request = new XMLHttpRequest();
    request.addEventListener('load', function (event) {
        Swal.close();
        if (request.status >= 200 && request.status < 300) {
            Swal.fire({
                title: 'You are succesfuly logged out!',
                icon: "success",
                confirmButtonText: 'Reload',
                onAfterClose() {
                    window.location.reload()
                },
                showCancelButton: false,
                showCloseButton: false,
                showConfirmButton: true,
                keydownListenerCapture: false,
                allowOutsideClick: false,
            });
        } else {
            Swal.fire({
                title: 'Logout failed!',
                icon: "error",
                confirmButtonText: 'Reload',
                onAfterClose() {
                    window.location.reload()
                },
                showCancelButton: false,
                showCloseButton: false,
                showConfirmButton: true,
                keydownListenerCapture: false,
                allowOutsideClick: false,
            });
        }
    });
    request.open("GET", "/api/internal/logout", true);
    request.setRequestHeader("Content-Type", "application/json");
    request.send();
}


export {handleLogout}