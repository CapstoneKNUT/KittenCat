// store.js
import { configureStore, createSlice } from '@reduxjs/toolkit';

const pageSlice = createSlice({
    name: 'page',
    initialState: {
        pageRequest: {},
        ResponseData: {},
    },
    reducers: {
        setPageRequest(state, action) {
            state.pageRequest = action.payload;
        },
        setResponseData(state, action) {
            state.ResponseData = action.payload;
        },
    },
});

export const { setPageRequest, setResponseData } = pageSlice.actions;
export default configureStore({
    reducer: {
        page: pageSlice.reducer,
    },
});
