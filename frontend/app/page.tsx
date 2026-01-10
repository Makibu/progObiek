"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Input } from "@/components/ui/input";

type SolverType = "gauss" | "jacobi" | "gauss-seidel";

export default function HomePage() {
    const [solver, setSolver] = useState<SolverType>("gauss");
    const [rows, setRows] = useState<number>(3);
    const [cols, setCols] = useState<number>(3);
    const [matrix, setMatrix] = useState<string[][]>(Array.from({ length: 3 }, () => Array.from({ length: 3 }, () => "")));
    const [vector, setVector] = useState<string[]>(Array(3).fill(""));
    const [result, setResult] = useState<any>(null);
    const [error, setError] = useState<string>("");

    const updateSize = (newRows: number, newCols: number) => {
        setRows(newRows);
        setCols(newCols);
        setMatrix(Array.from({ length: newRows }, () => Array.from({ length: newCols }, () => "")));
        setVector(Array(newRows).fill(""));
    };

    const handleMatrixChange = (i: number, j: number, value: string) => {
        const newMatrix = matrix.map((row, rowIndex) =>
            row.map((cell, colIndex) => (rowIndex === i && colIndex === j ? value : cell))
        );
        setMatrix(newMatrix);
    };

    const handleVectorChange = (i: number, value: string) => {
        const newVector = vector.map((v, index) => (index === i ? value : v));
        setVector(newVector);
    };

    const floatRegex = /^-?\d+(\.\d+)?$/;

    const validateInput = (): string | null => {
        if (rows < 1 || cols < 1) return "Rows and columns must be positive numbers.";

        for (let i = 0; i < rows; i++) {
            for (let j = 0; j < cols; j++) {
                const val = matrix[i][j].trim();
                if (!val) return `Matrix cell [${i + 1}, ${j + 1}] is empty.`;
                if (!floatRegex.test(val)) return `Matrix cell [${i + 1}, ${j + 1}] is not a valid number.`;
            }
        }

        for (let i = 0; i < rows; i++) {
            const val = vector[i].trim();
            if (!val) return `Vector b element [${i + 1}] is empty.`;
            if (!floatRegex.test(val)) return `Vector b element [${i + 1}] is not a valid number.`;
        }

        if (vector.length !== rows) return "Vector b length must equal number of rows.";

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
            if (!res.ok) throw new Error(`Server returned ${res.status}`);
            const data = await res.json();
            setResult(data);
        } catch (err) {
            setError((err as Error).message);
            setResult(null);
        }
    };

    // --- Obsługa pliku ---
    const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = (ev) => {
            const text = ev.target?.result as string;
            const lines = text.split(/\r?\n/).filter(line => line.trim() !== "");

            if (lines.length < 1) {
                setError("File is empty or invalid format.");
                return;
            }

            // Pierwsza linia = wymiary
            const dims = lines[0].trim().split(/\s+/).map(Number);
            if (dims.length !== 2 || dims.some(isNaN)) {
                setError("First line must contain two numbers: rows cols.");
                return;
            }

            const [fileRows, fileCols] = dims;
            updateSize(fileRows, fileCols);

            // Sprawdzenie czy plik ma wystarczająco wierszy danych
            if (lines.length < fileRows + 1) {
                setError("File does not contain enough rows for the matrix.");
                return;
            }

            // Wczytywanie macierzy
            const newMatrix: string[][] = [];
            for (let i = 0; i < fileRows; i++) {
                const rowVals = lines[i + 1].trim().split(/\s+/);
                if (rowVals.length !== fileCols) {
                    setError(`Row ${i + 1} does not contain ${fileCols} values.`);
                    return;
                }
                newMatrix.push(rowVals);
            }
            setMatrix(newMatrix);

            // Wczytywanie wektora b
            const newVector: string[] = [];
            for (let i = 0; i < fileRows; i++) {
                const val = lines[fileRows + i + 1]?.trim();
                if (!val) {
                    setError(`Missing vector b element for row ${i + 1}.`);
                    return;
                }
                newVector.push(val);
            }
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
                <input type="file" accept=".txt" onChange={handleFileUpload} className={'border-1 rounded-md border-black px-3'}/>
            </div>

            <div className="space-y-2">
                <label className="block font-semibold">Select Solver:</label>
                <Select onValueChange={(v) => setSolver(v as SolverType)} value={solver}>
                    <SelectTrigger>
                        <SelectValue placeholder="Choose solver" />
                    </SelectTrigger>
                    <SelectContent>
                        <SelectItem value="gauss">Gauss</SelectItem>
                        <SelectItem value="jacobi">Jacobi</SelectItem>
                        <SelectItem value="gauss-seidel">Gauss-Seidel</SelectItem>
                    </SelectContent>
                </Select>
            </div>

            <div className="flex gap-4 items-center">
                <div className="space-y-1">
                    <label className="block font-semibold">Rows (n):</label>
                    <Input type="number" min={1} max={10} value={rows} onChange={(e) => updateSize(Number(e.target.value), cols)} />
                </div>
                <div className="space-y-1">
                    <label className="block font-semibold">Columns (m):</label>
                    <Input type="number" min={1} max={10} value={cols} onChange={(e) => updateSize(rows, Number(e.target.value))} />
                </div>
            </div>

            <div className="space-y-2">
                <label className="block font-semibold">Matrix A:</label>
                <div className="grid gap-2" style={{ gridTemplateColumns: `repeat(${cols}, 1fr)` }}>
                    {matrix.map((row, i) =>
                        row.map((val, j) => (
                            <Input key={`${i}-${j}`} value={val} onChange={(e) => handleMatrixChange(i, j, e.target.value)} />
                        ))
                    )}
                </div>
            </div>

            <div className="space-y-2">
                <label className="block font-semibold">Vector b:</label>
                <div className="grid gap-2" style={{ gridTemplateColumns: `repeat(1, 1fr)` }}>
                    {vector.map((val, i) => (
                        <Input key={i} value={val} onChange={(e) => handleVectorChange(i, e.target.value)} />
                    ))}
                </div>
            </div>

            <Button onClick={handleSubmit}>Solve</Button>

            {error && <div className="mt-4 p-4 bg-red-100 text-red-700 rounded">{error}</div>}

            {result && (
                <div className="mt-4 space-y-4">
                    {result.solution ? (
                        <>
                            <div className="flex flex-wrap gap-2">
                                {result.solution.map((val: number, i: number) => (
                                    <div key={i} className="px-3 py-2 bg-green-100 text-green-900 rounded shadow">
                                        x{i + 1}: {val}
                                    </div>
                                ))}
                            </div>
                        </>
                    ) : (
                        <div className="p-4 bg-yellow-100 text-yellow-900 rounded shadow">
                            {result.message || "No solution available."}
                        </div>
                    )}

                    <div className="p-3 bg-gray-100 rounded shadow space-y-1">
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
