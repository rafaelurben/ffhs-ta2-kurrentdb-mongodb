// Projection that keeps track of a list of all parents and their children in a single state.
// Docs: https://docs.kurrent.io/server/v25.0/features/projections/custom.html

let prefix = "%s";
let splitPrefix = prefix + "-";

fromCategory(prefix)
    .when({
        $init: function () {
            return {
                parents: {}
            };
        },

        ParentCreated: function (s, e) {
            const id = e.streamId.split(splitPrefix)[1];
            if (s.parents[id]) {
                throw "ImpossibleHistoryException: Parent already exists";
            }
            s.parents[id] = e.data.createData;
        },

        ParentUpdated: function (s, e) {
            const id = e.streamId.split(splitPrefix)[1];
            const parent = s.parents[id];
            if (!parent) {
                throw "ImpossibleHistoryException: Parent is null when trying to update";
            }
            Object.assign(parent, e.data.changeData);
        },

        ParentDeleted: function (s, e) {
            const id = e.streamId.split(splitPrefix)[1];
            if (!s.parents[id]) {
                throw "ImpossibleHistoryException: Parent is null when trying to delete";
            }
            delete s.parents[id];
        },

        ChildCreated: function (s, e) {
            const id = e.streamId.split(splitPrefix)[1];
            const parent = s.parents[id];
            if (!parent) {
                throw "ImpossibleHistoryException: Parent is null when trying to create child";
            }
            parent.children = parent.children || [];
            parent.children.push(e.data.createData);
        },

        ChildUpdated: function (s, e) {
            const id = e.streamId.split(splitPrefix)[1];
            const parent = s.parents[id];
            if (!parent) {
                throw "ImpossibleHistoryException: Parent is null when trying to update child";
            }
            const child = (parent.children || []).find(c => c.id === e.data.childId);
            if (!child) {
                throw "ImpossibleHistoryException: Child not found for update";
            }
            child.name = e.data.changeData.name;
            child.value = child.value + e.data.changeData.valueChange;
        },

        ChildDeleted: function (s, e) {
            const id = e.streamId.split(splitPrefix)[1];
            const parent = s.parents[id];
            if (!parent) {
                throw "ImpossibleHistoryException: Parent is null when trying to delete child";
            }
            const idx = (parent.children || []).findIndex(c => c.id === e.data.childId);
            if (idx === -1) {
                throw "ImpossibleHistoryException: Child not found for deletion";
            }
            parent.children.splice(idx, 1);
        }
    })
    .transformBy(function (state) {
        return {parents: Object.values(state.parents)}
    })
    .outputState()
