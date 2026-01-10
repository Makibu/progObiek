"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Input } from "@/components/ui/input";

type SolverType = "gauss" | "jacobi" | "gauss-seidel";

const createMatrix = (n: number) =>
    Array.from({ length: n }, () => Array.from({ length: n }, () => ""));

export default function HomePage() {
    const [solver, setSolver] = useState<SolverType>("gauss");
    const [n, setN] = useState<number>(3);
    const [matrix, setMatrix] = useState<string[][]>(createMatrix(3));
    const [vector, setVector] = useState<string[]>(Array(3).fill(""));
    const [result, setResult] = useState<any>(null);
    const [error, setError] = useState<string>("");

    const updateSize = (newN: number) => {
        setN(newN);
        setMatrix(createMatrix(newN));
        setVector(Array(newN).fill(""));
    };

    const handleMatrixChange = (i: number, j: number, value: string) => {
        setMatrix(prev =>
            prev.map((row, ri) =>
                row.map((cell, cj) => (ri === i && cj === j ? value : cell))
            )
        );
    };

    const handleVectorChange = (i: number, value: string) => {
        setVector(prev => prev.map((v, idx) => (idx === i ? value : v)));
    };

    const floatRegex = /^-?\d+(\.\d+)?$/;

    const validateInput = (): string | null => {
        for (let i = 0; i < n; i++) {
            for (let j = 0; j < n; j++) {
                const val = matrix[i][j].trim();
                if (!val) return `Matrix cell [${i + 1}, ${j + 1}] is empty.`;
                if (!floatRegex.test(val)) return `Matrix cell [${i + 1}, ${j + 1}] is not a valid number.`;
            }

            const bVal = vector[i].trim();
            if (!bVal) return `Vector b element [${i + 1}] is empty.`;
            if (!floatRegex.test(bVal)) return `Vector b element [${i + 1}] is not a valid number.`;
        }
        return null;
    };

    const handleSubmit = async () => {
        const validationError = validateInput();
        if (validationError) {
            setError(validationError);
            setResult(null);
            return;
        }

        setError("");

        const A = matrix.map(row => row.map(Number));
        const b = vector.map(Number);

        try {
            const res = await fetch("http://localhost:8080/solve", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ solver, A, b }),
            });
            if (!res.ok) {
                const data = await res.json().catch(() => null);
                setError(data?.message ?? `Request failed (${res.status})`);
                setResult(null);
                return;
            }
            setResult(await res.json());
        } catch (err) {
            setError((err as Error).message);
            setResult(null);
        }
    };

    // ---- File upload ----
    const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = ev => {
            const text = ev.target?.result as string;
            const lines = text.split(/\r?\n/).filter(l => l.trim() !== "");

            if (lines.length < 1) {
                setError("File is empty.");
                return;
            }

            const fileN = Number(lines[0].trim());
            if (!Number.isInteger(fileN) || fileN <= 0) {
                setError("First line must contain a single positive integer (matrix size).");
                return;
            }

            if (lines.length < 1 + fileN * 2) {
                setError("File does not contain enough data.");
                return;
            }

            updateSize(fileN);

            const newMatrix: string[][] = [];
            for (let i = 0; i < fileN; i++) {
                const row = lines[i + 1].trim().split(/\s+/);
                if (row.length !== fileN) {
                    setError(`Matrix row ${i + 1} must contain ${fileN} values.`);
                    return;
                }
                newMatrix.push(row);
            }

            const newVector: string[] = [];
            for (let i = 0; i < fileN; i++) {
                newVector.push(lines[1 + fileN + i].trim());
            }

            setMatrix(newMatrix);
            setVector(newVector);
            setError("");
        };

        reader.readAsText(file);
    };

    return (
        <div className="max-w-5xl mx-auto p-6 space-y-6">
            <h1 className="text-2xl font-bold">Linear System Solver</h1>

            <div className="space-y-2">
                <label className="block font-semibold">Upload matrix file:</label>
                <input type="file" accept=".txt" onChange={handleFileUpload} className="border rounded-md px-3" />
            </div>

            <div className="space-y-2">
                <label className="block font-semibold">Select Solver:</label>
                <Select value={solver} onValueChange={v => setSolver(v as SolverType)}>
                    <SelectTrigger>
                        <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                        <SelectItem value="gauss">Gauss</SelectItem>
                        <SelectItem value="jacobi">Jacobi</SelectItem>
                        <SelectItem value="gauss-seidel">Gauss-Seidel</SelectItem>
                    </SelectContent>
                </Select>
            </div>

            <div className="space-y-1">
                <label className="block font-semibold">Matrix size (n × n):</label>
                <Input type="number" min={1} max={10} value={n} onChange={e => updateSize(Number(e.target.value))} />
            </div>

            <div className="space-y-2">
                <label className="block font-semibold">Matrix A:</label>
                <div className="grid gap-2" style={{ gridTemplateColumns: `repeat(${n}, 1fr)` }}>
                    {matrix.map((row, i) =>
                        row.map((val, j) => (
                            <Input key={`${i}-${j}`} value={val} onChange={e => handleMatrixChange(i, j, e.target.value)} />
                        ))
                    )}
                </div>
            </div>

            <div className="space-y-2">
                <label className="block font-semibold">Vector b:</label>
                <div className="grid gap-2">
                    {vector.map((val, i) => (
                        <Input key={i} value={val} onChange={e => handleVectorChange(i, e.target.value)} />
                    ))}
                </div>
            </div>

            <Button onClick={handleSubmit}>Solve</Button>

            {error && <div className="p-4 bg-red-100 text-red-700 rounded">{error}</div>}

            {result && (
                <div className="space-y-4">
                    {result.solution && (
                        <div className="flex flex-wrap gap-2">
                            {result.solution.map((v: number, i: number) => (
                                <div key={i} className="px-3 py-2 bg-green-100 rounded">
                                    x{i + 1}: {v}
                                </div>
                            ))}
                        </div>
                    )}

                    <div className="p-3 bg-gray-100 rounded space-y-1">
                        <div><strong>Residual:</strong> {result.residual}</div>
                        <div><strong>Message:</strong> {result.message}</div>
                        <div><strong>Iterations:</strong> {result.iterations}</div>
                        {result.determinant !== undefined && <div><strong>Determinant:</strong> {result.determinant}</div>}
                        {result.rank !== undefined && <div><strong>Rank:</strong> {result.rank}</div>}
                    </div>
                </div>
            )}
        </div>
    );
}
