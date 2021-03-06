package rta.tid;

import rta.CrystalAddr;

import rta.gambatte.Gb;
import rta.gambatte.LoadFlags;

import java.io.*;
import java.util.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CrystalTIDManip {
    private static final int NO_INPUT = 0x00;
    private static final int A = 0x01;
    private static final int B = 0x02;
    private static final int SELECT = 0x04;
    private static final int START = 0x08;
    private static final int UP = 0x40;
    

    // Change this to increase/decrease number of intro sequence combinations processed
    private static final int MAX_COST = 3600;

    private static final int BASE_COST = 387 + 60;

    private static Strat gfSkip =
	new Strat("_gfskip", 0,
	new Integer[] {CrystalAddr.readJoypadAddr},
	new Integer[] {START},
	new Integer[] {1});

    private static Strat gfWait =
	new Strat("_gfwait", 384,
	new Integer[] {CrystalAddr.introScene0Addr, CrystalAddr.readJoypadAddr},
	new Integer[] {NO_INPUT, START},
	new Integer[] {0, 1});

    private static Strat intro0 =
	new Strat("_intro0", 450,
	new Integer[] {CrystalAddr.introScene1Addr, CrystalAddr.readJoypadAddr},
	new Integer[] {NO_INPUT, START},
	new Integer[] {0, 1});

    private static Strat intro2 =
	new Strat("_intro1", 624,
	new Integer[] {CrystalAddr.introScene3Addr, CrystalAddr.readJoypadAddr},
	new Integer[] {NO_INPUT, START},
	new Integer[] {0, 1});

    private static Strat intro4 =
	new Strat("_intro2", 819,
	new Integer[] {CrystalAddr.introScene5Addr, CrystalAddr.readJoypadAddr},
	new Integer[] {NO_INPUT, START},
	new Integer[] {0, 1});

    private static Strat intro6 =
	new Strat("_intro3", 1052,
	new Integer[] {CrystalAddr.introScene7Addr, CrystalAddr.readJoypadAddr},
	new Integer[] {NO_INPUT, START},
	new Integer[] {0, 1});

    private static Strat intro10 =
	new Strat("_intro4", 1396,
	new Integer[] {CrystalAddr.introScene11Addr, CrystalAddr.readJoypadAddr},
	new Integer[] {NO_INPUT, START},
	new Integer[] {0, 1});

    private static Strat intro12 =
	new Strat("_intro5", 1674,
	new Integer[] {CrystalAddr.introScene13Addr, CrystalAddr.readJoypadAddr},
	new Integer[] {NO_INPUT, START},
	new Integer[] {0, 1});

    private static Strat intro14 =
	new Strat("_intro6", 1871,
	new Integer[] {CrystalAddr.introScene15Addr, CrystalAddr.readJoypadAddr},
	new Integer[] {NO_INPUT, START},
	new Integer[] {0, 1});

    private static Strat intro16 =
	new Strat("_intro7", 2085,
	new Integer[] {CrystalAddr.introScene17Addr, CrystalAddr.readJoypadAddr},
	new Integer[] {NO_INPUT, START},
	new Integer[] {0, 1});

    private static Strat intro18 =
	new Strat("_intro8", 2254,
	new Integer[] {CrystalAddr.introScene19Addr, CrystalAddr.readJoypadAddr},
	new Integer[] {NO_INPUT, START},
	new Integer[] {0, 1});

    private static Strat intro25 =
	new Strat("_intro9", 2565,
	new Integer[] {CrystalAddr.introScene26Addr, CrystalAddr.readJoypadAddr},
	new Integer[] {NO_INPUT, START},
	new Integer[] {0, 1});

    private static Strat introwait =
	new Strat("_introwait", 2827,
	new Integer[] {CrystalAddr.titleScreenAddr},
	new Integer[] {NO_INPUT},
	new Integer[] {0});

    private static Strat titleSkip =
	new Strat("_title", 54,
	new Integer[] {CrystalAddr.readJoypadAddr},
	new Integer[] {START},
	new Integer[] {1});

//  private static Strat titleSkip =
//	new Strat("", 54,
//	new Integer[] {CrystalAddr.readJoypadAddr},
//	new Integer[] {START},
//	new Integer[] {1});

    private static Strat titleUsb =
	new Strat("_title(usb)", 54,
	new Integer[] {CrystalAddr.readJoypadAddr},
	new Integer[] {UP | SELECT | B},
	new Integer[] {1});

    private static Strat csCancelA =
	new Strat("_cscancel(a)", 69,
	new Integer[] {CrystalAddr.printLetterDelayAddr, CrystalAddr.noYesBoxAddr, CrystalAddr.readJoypadAddr},
	new Integer[] {B, B, A},
	new Integer[] {0, 0, 1});

    private static Strat csCancelB =
	new Strat("_cscancel(b)", 69,
	new Integer[] {CrystalAddr.printLetterDelayAddr, CrystalAddr.noYesBoxAddr, CrystalAddr.readJoypadAddr},
	new Integer[] {A, A, B},
	new Integer[] {0, 0, 1});

    private static Strat newGame =
	new Strat("_newgame", 8,
	new Integer[] {CrystalAddr.readJoypadAddr, CrystalAddr.postLID}, // TID is already genned
	new Integer[] {A, NO_INPUT},
	new Integer[] {1, 0});

    private static Strat backout =
	new Strat("_backout", 44,
	new Integer[] {CrystalAddr.readJoypadAddr},
	new Integer[] {B},
	new Integer[] {1});

    private static List<Strat> intro = Arrays.asList(gfSkip, gfWait, intro0, intro2, intro4, intro6, intro10, intro12, intro14, intro16, intro18, intro25, introwait);

    static class Strat {
        String name;
        int cost;
        Integer[] addr;
        Integer[] input;
        Integer[] advanceFrames;
        Strat(String name, int cost, Integer[] addr, Integer[] input, Integer[] advanceFrames) {
            this.addr = addr;
            this.cost = cost;
            this.name = name;
            this.input = input;
            this.advanceFrames = advanceFrames;
        }
        public void execute(Gb gb) {
            for(int i=0; i<addr.length; i++) {
                gb.advanceWithJoypadToAddress(input[i], addr[i]);
                for(int j=0; j<advanceFrames[i]; j++) {
                    gb.advanceFrame(input[i]);
                }
            }
        }
    }

    static class IntroSequence extends ArrayList<Strat> implements Comparable<IntroSequence> {
        IntroSequence(Strat... strats) {
            super(Arrays.asList(strats));
        }
        IntroSequence(IntroSequence other) {
            super(other);
        }
        @Override public String toString() {
            String ret = "crystal";
            for(int i=0; i<this.size(); i++) {
                Strat s = this.get(i);
                if(s.name.equals(("_backout"))) {
                    int backoutCounter = 0;
                    while(s.name.equals("_backout")) {
                        backoutCounter += 1;
                        i += 2;
                        s = this.get(i);
                    }
                    ret += "_backout" + backoutCounter;
                }
                ret += s.name;
            }
            return ret;
        }
        void execute(Gb gb) {
            for(Strat s : this) {
                s.execute(gb);
            }
        }
        int cost() {
            return this.stream().mapToInt((Strat s) -> s.cost).sum();
        }
        @Override public int compareTo(IntroSequence o) {
            return this.cost() - o.cost();
        }
    }

    private static IntroSequence append(IntroSequence seq, Strat... strats) {
        IntroSequence newSeq = new IntroSequence(seq);
        newSeq.addAll(Arrays.asList(strats));
        return newSeq;
    }

    private static void addWaitPermutations(ArrayList<IntroSequence> introSequences, IntroSequence introSequence) {
        int ngmax = (MAX_COST - (introSequence.cost() + BASE_COST + 8));
        for(int i=0; ngmax>=0 && i<=ngmax/98; i++) {
            introSequences.add(append(introSequence, newGame));
            introSequence = append(introSequence, backout, titleSkip);
        }
    }

    private static void addOptPermutations(ArrayList<IntroSequence> introSequences, IntroSequence introSequence) {
        int ngmax = (MAX_COST - (introSequence.cost() + BASE_COST + 8 + 95));
        for(int i=0; ngmax>=0 && i<=ngmax/98; i++) {
            introSequences.add(append(introSequence, newGame));
            introSequence = append(introSequence, backout, titleSkip);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if (!new File("roms").exists()) {
            new File("roms").mkdir();
            System.err.println("I need ROMs to simulate!");
            System.exit(0);
        }

        File file = new File("crystal_tids.txt");
        PrintWriter writer = new PrintWriter(file);

        ArrayList<Strat> waitStrats = new ArrayList<>();
        int maxwaits = (MAX_COST - BASE_COST - 54 - 8)/4;
        for(int i=1; i<=maxwaits; i++) {
            Integer[] addr = new Integer[i];
            Integer[] input = new Integer[i];
            Integer[] advFrames = new Integer[i];
            for(int j=0; j<i; j++) {
                addr[j] = CrystalAddr.mainMenuJoypadAddr;
                input[j] = NO_INPUT;
                advFrames[j] = 1;
            }
            waitStrats.add(new Strat("_wait" + i, i*4, addr, input, advFrames));
        }

        ArrayList<Strat> optStrats = new ArrayList<>();
        for(int i=1; i<=maxwaits; i++) {
            Integer[] addr = new Integer[i+2];
            Integer[] input = new Integer[i+2];
            Integer[] advFrames = new Integer[i+2];
            addr[0] = CrystalAddr.readJoypadAddr;
            input[0] = 0x80;
            advFrames[0] = 1;
            for(int j=1; j<i; j++) {
                addr[j] = CrystalAddr.mainMenuJoypadAddr;
                input[j] = NO_INPUT;
                advFrames[j] = 1;
            }
            addr[i] = CrystalAddr.readJoypadAddr;
            input[i] = A;
            advFrames[i] = 1;
            addr[i+1] = CrystalAddr.readJoypadAddr;
            input[i+1] = START;
            advFrames[i+1] = 1;
            optStrats.add(new Strat("_wait" + i + "(opt)", i*4 + 95, addr, input, advFrames));
        }

        ArrayList<IntroSequence> introSequences = new ArrayList<>();
        for(Strat s : intro) {
            IntroSequence introSequence = new IntroSequence(s, titleSkip);
            int ngmax = (MAX_COST - (introSequence.cost() + BASE_COST + 8));
            for(int i=0; ngmax>=0 && i<=ngmax/98; i++) {
                introSequences.add(append(introSequence, newGame));
                for(Strat s2 : waitStrats) {
                    IntroSequence base = append(introSequence, s2);
                    addWaitPermutations(introSequences, base);
                }
                for(Strat s3 : optStrats) {
                    IntroSequence base = append(introSequence, s3);
                    addOptPermutations(introSequences, base);
                }
                introSequence = append(introSequence, backout, titleSkip);
            }   
        }
        ArrayList<IntroSequence> resetSequences = new ArrayList<>();
        {   for(IntroSequence s3 : resetSequences) {
                int ngcost = s3.cost() + BASE_COST;
                int rscost = ngcost + BASE_COST;
                int rsmax = (MAX_COST - rscost);
                if(rsmax >= 0) {
                    resetSequences.add(append(s3, titleUsb, csCancelA));
                    resetSequences.add(append(s3, titleUsb, csCancelB));
                }
            }
        }
        Collections.sort(resetSequences);

        System.out.println("Number of intro sequences: " + introSequences.size());
        Collections.sort(introSequences);

        // Init gambatte with 1 session
        Gb gb = new Gb();
        gb.loadBios("roms/gbc_bios.bin");
        gb.loadRom("roms/pokecrystal.gbc", LoadFlags.DEFAULT_LOAD_FLAGS);
        gb.advanceToAddress(CrystalAddr.initAddr);
        byte[] postBios = gb.saveState();
        for(IntroSequence seq : introSequences) {
            seq.execute(gb);
            int tid = readTID(gb);
            int lid = readLID(gb);
            writer.println(
                    seq.toString()
                            + ": TID = " + String.format("0x%4s", Integer.toHexString(tid).toUpperCase()).replace(' ', '0') + " (" + String.format("%5s)", tid).replace(' ', '0')
                            + ", LID = " + String.format("0x%4s", Integer.toHexString(lid).toUpperCase()).replace(' ', '0') + " (" + String.format("%5s)", lid).replace(' ', '0')
                            + ", Cost: " + String.format("%.02f", (gb.getGbpTime() - 0.21)));
            gb.loadState(postBios);
            writer.flush();
            System.out.printf("Current Cost: %d%n", seq.cost());
        }
        writer.close();
    }

    private static int readTID(Gb gb) {
        return (gb.readMemory(0xD47B) << 8) | gb.readMemory(0xD47C);
    }

    private static int readLID(Gb gb) {
        return (gb.readMemory(0xDC9F) << 8) | gb.readMemory(0xDCA0);
    }
}
