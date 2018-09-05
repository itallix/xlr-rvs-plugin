class ReleaseValueStreamTileController {
    static $inject = ['$scope', 'Backend', 'ConfigurationItemService', 'PhasesService', 'ValueStreamMapping', 'CisLoader', 'ReleasesService'];

    constructor($scope, Backend, ConfigurationItemService, PhasesService, ValueStreamMapping, CisLoader, ReleasesService) {
        this.tileName = "Release value stream tile";
        this.xlrTile = $scope.xlrTile;
        this._ConfigurationItemService = ConfigurationItemService;
        this._PhasesService = PhasesService;
        this._ValueStreamMapping = ValueStreamMapping;
        this._CisLoader = CisLoader;

        this.releasesLoader = new CisLoader({
            searchCis: ReleasesService.searchArchived,
            filters: {},
            updateView: (releases) => {
                this.release = _.sortBy(releases, (release) => release.endDate).reverse()[0];
            }
        });
        this.releasesLoader.loadNextCis();
    }

    load(config) {
        this.loading = true;
        const tileId = this.xlrTile.tile.id;
        // Backend.get("tiles/" + tileId + "/data", config).then(
        //     function(response) {
        //         vm.data = response.data.data;
        //     }
        // ).finally(function() {
        //         vm.loading = false;
        // });
    }

    refresh() {
        load({params: {refresh: true}});
    }

    getDurationPercentage(release, phase) {
        return this._PhasesService.getDurationPercentage(release, phase);
    };

    getLeafTasks(phase) {
        return this._PhasesService.getLeafTasks(phase);
    }

    isCritical(phase) {
        this._ValueStreamMapping.isCritical(phase);
    }

    getWarningThreshold(release) {
        this._ValueStreamMapping.getWarningThreshold(release);
    }

    getErrorThreshold(release) {
        this._ValueStreamMapping.getErrorThreshold(release);
    }

    getCriticalPhasesCount(release) {
        this._ValueStreamMapping.getCriticalPhasesCount(release);
    }

    getCIDuration(phase) {
        this._ConfigurationItemService.getCIDuration(phase);
    }
}

angular.module('xlrelease.rvsTile', []);
angular.module('xlrelease.rvsTile').controller('rvsTile.ReleaseValueStreamTileController', ReleaseValueStreamTileController);
