import axios from "axios";

async function uploadToServer (formObj) {

    console.log("upload to server......")
    console.log(formObj)

    const response = await axios({
        method: 'post',
        url: 'http://localhost:8080/api/upload',
        data: formObj,
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    });

    return response.data
}

async function removeFileToServer(uuid, fileName){

    const response = await axios.delete(`http://localhost:8080/api/remove/${uuid}_${fileName}`)

    return response.data

}

export {uploadToServer, removeFileToServer};