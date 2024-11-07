import axios from 'axios';
async function get1(bno) {

    const result = await axios.get(`http://localhost:8080/api/replies/list/${bno}`)

    //console.log(result)

    return result;
}

async function getList({bno, page, size, goLast, type}){
    try {
        const result = await axios.get(`http://localhost:8080/api/replies/list/${bno}`, {params: {page, size, type}})

        if(goLast){
            const total = result.data.total
            const lastPage = parseInt(Math.ceil(total/size))

            return getList({bno:bno, page:lastPage, size:size, type:type})

        }
        return result.data
    } catch (error) {
        console.error(error);
        throw error;
    }
}


async function addReply(replyObj) {
    try {
        const response = await axios.post(`http://localhost:8080/api/replies/`,replyObj)
        return response.data
    } catch (error) {
        console.error(error);
        throw error;
    }
}

async function getReply(rno) {
    try {
        const response = await axios.get(`http://localhost:8080/api/replies/${rno}`)
        return response.data
    } catch (error) {
        console.error(error);
        throw error;
    }
}

async function modifyReply(replyObj) {
    try {
        const response = await axios.put(`http://localhost:8080/api/replies/${replyObj.rno}`, replyObj)
        return response.data
    } catch (error) {
        console.error(error);
        throw error;
    }
}

async function removeReply(rno) {
    try {
        const response = await axios.delete(`http://localhost:8080/api/replies/${rno}`)
        return response.data
    } catch (error) {
        console.error(error);
        throw error;
    }
}

export {getList, modifyReply, getReply, removeReply, addReply};