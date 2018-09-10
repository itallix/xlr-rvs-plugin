/**
 * Copyright (c) 2018. All rights reserved.
 *
 * This software and all trademarks, trade names, and logos included herein are the property of XebiaLabs, Inc. and its affiliates, subsidiaries, and licensors.
 */

class ReleaseValueStreamTileController {
    static $inject = ['$scope', 'Backend', 'ConfigurationItemService', 'PhasesService', 'ValueStreamMapping',
        'CisLoader', 'ReleasesService', 'ReportTileService'];

    constructor($scope, Backend, ConfigurationItemService, PhasesService, ValueStreamMapping, CisLoader,
                ReleasesService, ReportTileService) {
        this.tileName = "Release value stream tile";
        this.xlrTile = $scope.xlrTile;
        this._ConfigurationItemService = ConfigurationItemService;
        this._PhasesService = PhasesService;
        this._ValueStreamMapping = ValueStreamMapping;
        this._ReportTileService = ReportTileService;
        this._Backend = Backend;
        this._$scope = $scope;

        $scope.getDurationPercentage = this.getDurationPercentage.bind(this);

        this.load();
    }

    load(config) {
        this.loading = true;
        this._Backend.post("rvs/search", this._ReportTileService.getTileParams(this._$scope.tile))
            .then(resp => {
                if (resp.data.cis.length > 0) {
                    this.release = resp.data.cis[0];
                    this._$scope.release = this.release;
                    this.releaseCount = resp.data.cis.length;
                    this._$scope.releaseCount = this.releaseCount;
                }
            })
            .finally(() => { this.loading = false });
    }

    refresh() {
        load({params: {refresh: true}});
    }

    getDurationPercentage(release, phase) {
        if (release && phase) {
            const ciDuration = this._ConfigurationItemService.getCIDuration(release);
            return ciDuration === 0 ? 0 : Math.round((this._ConfigurationItemService.getCIDuration(phase) * 100) / ciDuration);
        }
    };

    getLeafTasks(phase) {
        if (phase) {
            return this._PhasesService.getLeafTasks(phase);
        }
    }

    isCritical(phase) {
        if (phase) {
            return this._ValueStreamMapping.isCritical(phase);
        }
    }

    getWarningThreshold(release) {
        if (release) {
            return this._ValueStreamMapping.getWarningThreshold(release);
        }
    }

    getErrorThreshold(release) {
        if (release) {
            return this._ValueStreamMapping.getErrorThreshold(release);
        }
    }

    getCriticalPhasesCount(release) {
        if (release) {
            return this._ValueStreamMapping.getCriticalPhasesCount(release);
        }
    }

    getCIDuration(phase) {
        if (phase) {
            return this._ConfigurationItemService.getCIDuration(phase);
        }
    }

    isLongestPhase(phase) {
        if (this.release && phase) {
            const current = this.getCIDuration(phase);
            const durations = [];
            this.release.phases.forEach(p => durations.push(this.getCIDuration(p)));
            return (Math.max(...durations) === current);
        }
    }
}

angular.module('xlrelease.rvsTile', []);
angular.module('xlrelease.rvsTile').controller('rvsTile.ReleaseValueStreamTileController', ReleaseValueStreamTileController);
