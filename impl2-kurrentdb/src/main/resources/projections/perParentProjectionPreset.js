// Projection that keeps track of the state of all parents and their children in their own partitions.
// Docs: https://docs.kurrent.io/server/v25.0/features/projections/custom.html

let prefix = "%s";
let splitPrefix = prefix + "-";

fromCategory(prefix)
    .partitionBy(e => e.streamId.split(splitPrefix)[1])
    .when({
        $init: function () {
            return {
                parent: null
            };
        },

        ParentCreated: function (s, e) {
            if (s.parent !== null) {
                throw "ImpossibleHistoryException: Parent already exists";
            }
            s.parent = e.data.createData;
        },

        ParentUpdated: function (s, e) {
            if (s.parent === null) {
                throw "ImpossibleHistoryException: Parent is null when trying to update";
            }
            Object.assign(s.parent, e.data.changeData);
        },

        ParentDeleted: function (s, e) {
            if (s.parent === null) {
                throw "ImpossibleHistoryException: Parent is null when trying to delete";
            }
            s.parent = null;
        },

        ChildCreated: function (s, e) {
            if (s.parent === null) {
                throw "ImpossibleHistoryException: Parent is null when trying to create child";
            }
            s.parent.children = s.parent.children || [];
            s.parent.children.push(e.data.createData);
        },

        ChildUpdated: function (s, e) {
            if (s.parent === null) {
                throw "ImpossibleHistoryException: Parent is null when trying to update child";
            }
            const child = (s.parent.children || []).find(c => c.id === e.data.childId);
            if (!child) {
                throw "ImpossibleHistoryException: Child not found for update";
            }
            child.name = e.data.changeData.name;
            child.value = child.value + e.data.changeData.valueChange;
        },

        ChildDeleted: function (s, e) {
            if (s.parent === null) {
                throw "ImpossibleHistoryException: Parent is null when trying to delete child";
            }
            const idx = (s.parent.children || []).findIndex(c => c.id === e.data.childId);
            if (idx === -1) {
                throw "ImpossibleHistoryException: Child not found for deletion";
            }
            s.parent.children.splice(idx, 1);
        }
    })
