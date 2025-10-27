import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class FeaturePermissionService {
  private assignedFeatures: string[] = [];

  setFeatures(features: string[]) {
    this.assignedFeatures = features;
  }

  getFeatures(): string[] {
    return this.assignedFeatures;
  }

  hasFeature(feature: string): boolean {
    return this.assignedFeatures.includes(feature);
  }
}
