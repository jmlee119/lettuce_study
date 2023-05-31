// 주소-좌표 변환 객체를 생성합니다
var geocoder = new kakao.maps.services.Geocoder();

// 현재 위치를 받아와서 좌표로 변환하고 주소를 표시합니다
function getLocationAndSaveLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(saveLocationToServer);
    } else {
        console.log("Geolocation is not supported by this browser.");
    }
}
function saveLocationToServer(position) {
    const latitude = position.coords.latitude;
    const longitude = position.coords.longitude;
    const location = `${latitude},${longitude}`;

    var mapContainer = document.getElementById('map'), // 지도를 표시할 div
        mapOption = {
            center: new kakao.maps.LatLng(latitude,longitude), // 지도의 중심좌표
            level:3 // 지도의 확대 레벨
        };

    // 지도를 표시할 div와  지도 옵션으로  지도를 생성합니다
    var map = new kakao.maps.Map(mapContainer, mapOption);

    var markerPosition  = new kakao.maps.LatLng(latitude,longitude);

// 마커를 생성합니다
    var marker = new kakao.maps.Marker({
        position: markerPosition
    });

// 마커가 지도 위에 표시되도록 설정합니다
    marker.setMap(map);

    // 변환된 좌표를 이용하여 주소를 검색하고 표시합니다
    var latlng = new kakao.maps.LatLng(latitude, longitude);
    searchAddrFromCoords(latlng, displayCenterInfo);
}

// 좌표로 주소를 검색하는 함수
function searchAddrFromCoords(coords, callback) {
    geocoder.coord2RegionCode(coords.getLng(), coords.getLat(), callback);
}

// 주소를 표시하는 함수
function displayCenterInfo(result, status) {
    if (status === kakao.maps.services.Status.OK) {
        var infoDiv = document.getElementById('centerAddr');
        infoDiv.innerHTML = result[0].address_name;
        var splitAddress = address.split(' ');
        var simplifiedAddress = splitAddress.slice(0, 2).join(' ');

        const url = '/members/location';

        fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(simplifiedAddress),
        })
            .then(response => response.json())
            .then(data => {
                console.log('Location saved:', data);
                // Redirect to the post creation page or perform any other action
            })
            .catch(error => {
                console.error('Error saving location:', error);
            });
    }
}