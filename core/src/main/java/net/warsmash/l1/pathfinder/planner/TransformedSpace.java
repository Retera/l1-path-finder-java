package net.warsmash.l1.pathfinder.planner;

public interface TransformedSpace {
	double getUnprojectedX(double x, double y);

	double getUnprojectedY(double x, double y);

	TransformedSpace IDENTITY = new TransformedSpace() {
		@Override
		public double getUnprojectedX(double x, double y) {
			return x;
		}

		@Override
		public double getUnprojectedY(double x, double y) {
			return y;
		}
	};
}
