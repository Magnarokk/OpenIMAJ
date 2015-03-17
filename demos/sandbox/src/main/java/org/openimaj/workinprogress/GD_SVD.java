/**
 * Copyright (c) 2011, The University of Southampton and the individual contributors.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *   * 	Redistributions of source code must retain the above copyright notice,
 * 	this list of conditions and the following disclaimer.
 *
 *   *	Redistributions in binary form must reproduce the above copyright notice,
 * 	this list of conditions and the following disclaimer in the documentation
 * 	and/or other materials provided with the distribution.
 *
 *   *	Neither the name of the University of Southampton nor the names of its
 * 	contributors may be used to endorse or promote products derived from this
 * 	software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openimaj.workinprogress;

import java.util.Random;

import Jama.Matrix;

public class GD_SVD {
	private static final int maxEpochs = 300;
	private static final double initialLearningRate = 0.01;
	private static final double annealingRate = maxEpochs * 0.1;

	Matrix UprimeM;
	Matrix VprimeM;
	private Matrix UM;
	private Matrix VM;
	private Matrix SM;

	public GD_SVD(Matrix MM, int maxOrder) {
		final Random random = new Random();
		final double initValue = 1 / Math.sqrt(maxOrder);

		final int m = MM.getRowDimension();
		final int n = MM.getColumnDimension();

		UprimeM = new Matrix(m, maxOrder);
		VprimeM = new Matrix(n, maxOrder);
		final double[][] Uprime = UprimeM.getArray();
		final double[][] Vprime = VprimeM.getArray();
		final double[][] M = MM.getArray();

		for (int k = 0; k < maxOrder; k++) {
			for (int i = 0; i < m; i++)
				Uprime[i][k] = random.nextGaussian() * initValue;
			for (int j = 0; j < n; j++)
				Vprime[j][k] = random.nextGaussian() * initValue;

			double lastError = Double.MAX_VALUE;
			for (int epoch = 0; epoch < maxEpochs; epoch++) {
				final double learningRate = initialLearningRate / (1 + epoch / annealingRate);

				double sq = 0;
				for (int i = 0; i < m; i++) {
					for (int j = 0; j < n; j++) {
						double pred = 0;
						for (int kk = 0; kk <= k; kk++)
							pred += Uprime[i][kk] * Vprime[j][kk];

						final double error = M[i][j] - pred;
						sq += error * error;
						final double uTemp = Uprime[i][k];
						final double vTemp = Vprime[j][k];
						// Uprime[i][k] += learningRate[epoch] * ( error * vTemp
						// - regularization * uTemp );
						// Vprime[j][k] += learningRate[epoch] * ( error * uTemp
						// - regularization * vTemp );
						Uprime[i][k] += learningRate * (error * vTemp);
						Vprime[j][k] += learningRate * (error * uTemp);
					}
				}

				if (lastError - sq < 0.000001)
					break;

				lastError = sq;
			}
		}

		UM = new Matrix(m, maxOrder);
		final double[][] U = UM.getArray();
		SM = new Matrix(maxOrder, maxOrder);
		final double[][] S = SM.getArray();
		VM = new Matrix(maxOrder, n);
		final double[][] V = VM.getArray();
		for (int i = 0; i < maxOrder; i++) {
			for (int j = 0; j < m; j++) {
				S[i][i] += (Uprime[j][i] * Uprime[j][i]);
			}
			S[i][i] = Math.sqrt(S[i][i]);
			for (int j = 0; j < m; j++) {
				U[j][i] = Uprime[j][i] / S[i][i];
			}
			for (int j = 0; j < n; j++) {
				V[i][j] = Vprime[j][i] / S[i][i];
			}
			S[i][i] = S[i][i] * S[i][i];
		}

	}

	public static void main(String[] args) {
		final Matrix m = Matrix.random(40, 40);

		final GD_SVD gdsvd = new GD_SVD(m, 10);

		// m.print(5, 5);
		// gdsvd.UprimeM.print(5, 5);
		// gdsvd.UprimeM.times(gdsvd.VprimeM.transpose()).print(5, 5);
		// gdsvd.UM.times(gdsvd.SM.times(gdsvd.VM)).print(5, 5);
		gdsvd.UM.print(5, 5);
		// gdsvd.SM.print(5, 5);
		gdsvd.VM.print(5, 5);

		// final ThinSingularValueDecomposition tsvd = new
		// ThinSingularValueDecomposition(m, 2);
		// tsvd.U.print(5, 5);
		// System.out.println(Arrays.toString(tsvd.S));

	}
}